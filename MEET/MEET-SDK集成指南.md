# 音视频 SDK 集成指南

音视频通话与会议（MiroTalk）、屏幕共享（WebRTC 采集 + SDP 代理）。不依赖 IM，也不依赖底座其它模块（`av-call` 只依赖 AndroidX、OkHttp）。

| artifact | 内容 |
|---|---|
| `av-call` | 音视频通话与会议（MiroTalk） |
| `av-screen-share` | 屏幕共享（WebRTC 采集 + SDP 代理） |

## 第一步：引入依赖（离线依赖包）

1. 下载本卡片目录 `MEET-SDK/` 下的 `MEET-SDK-maven-repo.zip` 并解压，得到 `maven-repo/` 离线仓库（含 com.oort 全部模块 AAR+POM 与内部依赖闭包）；
2. 在 `settings.gradle`（或根 `build.gradle`）中配置**完整**仓库清单：

```groovy
repositories {
    maven { url = uri('/解压路径/maven-repo') }   // com.oort 全部模块 + 内部依赖闭包，离线可用
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }           // swipeback / Luban / MPAndroidChart 等
    maven { url 'https://maven.aliyun.com/repository/jcenter' }   // jcenter 归档库
    maven { url 'https://developer.huawei.com/repo/' }            // 华为 HMS 推送
    maven { url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases" }
}
```

> **仓库清单必须完整**：缺 jitpack / aliyun-jcenter / 华为仓库会导致第三方依赖解析失败（已实测按此清单 289 个制品全部解析通过）。


3. 引入坐标（完整坐标清单见 `MEET-SDK/README.md`）：

```groovy
dependencies {
    implementation "com.oort.workup:av-call:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:av-screen-share:2.0.0-rc.20261117.3"

    // WebRTC 两者均保留 compileOnly 契约、不由 POM 传递，且公开接口含 org.webrtc.SessionDescription 等类型，宿主必须显式引入运行时与编译 classpath：
    implementation "io.github.webrtc-sdk:android:125.6422.07"
}
```

> 不要同时引入旧版 `org.webrtc:google-webrtc` 或另一套含相同 `org.webrtc.*` 类/native 库的产物；实际设备/ABI 与 16KB 页兼容性须对最终 APK 验证。

## 第二步：初始化

Application 继承底座的 `com.oort.login.common.app.WorkupBaseApplication`（不集成 IM 的宿主用这个；集成 IM 时请继承 IM 的 `MyApplication`），按需覆写环境预设：

```java
public class DemoApplication extends WorkupBaseApplication {

    @Override
    protected String projectPreset() {
        return BuildConfig.PROJECT_PRESET;   // 默认 OA_2443
    }
}
```

## 第三步：登录与主界面

1. 启动页为 SDK 登录模块的 `com.oort.login.common.activity.SplashActivity`，宿主无需再写启动页，直接使用即可：

```xml
<activity
    android:name="com.oort.login.common.activity.SplashActivity"
    android:exported="true"
    android:theme="@style/SplashTheme">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

2. 登录成功后通过隐式跳转 `${applicationId}.main` 进入主界面：
   - 集成门户时由门户主界面承接；
   - 未集成门户时由 SDK 自带主界面承接（音视频卡片需宿主自建主界面，通过 `ImHost.setMainActivityClass` 或 `${applicationId}.main` 承接）。

3. 服务器环境：默认预设 **OA_2443**，对应网关 `https://oa.oortcloudsmart.com:2443/`（业务、IM 接口走单端口 HTTPS 网关，消息长连接走 WSS）。私有化部署时按 `ProjectConfig` 中的预设模板配置自有域名。

4. 运行时权限：SDK 的 manifest 已合并全部权限声明，宿主**无需手工添加**；以下敏感权限由登录模块在启动页统一弹窗申请——消息通知、定位、麦克风、相机、媒体读写（Android 13+ 为 `READ_MEDIA_*`）。

5. 体验账号：`18948726601` / `12345678`（详见仓库根 README 与本卡片 `MEET-SDK-Demo/README.md` 的「体验账号」板块，2026-11-23 实机验证：登录、消息收发、通讯录均正常）。

## 会议调用

```java
CallSdkConfig config = new CallSdkConfig.Builder()
        .callType(CallSdkConfig.CALL_TYPE_VIDEO_MEET)
        .apiServer(apiServer)          // 会议服务地址
        .accessToken(token)
        .userId(userId).userName(name)
        .meetingTag(meetingTag)        // 加入已有会议时传；发起时为 null，SDK 回调 onMeetingTagCreated
        .isInitiator(meetingTag == null)
        .enableScreenShare(true)
        .listener(listener)
        .build();
CallSdk.startCall(context, config);
```

## 独立投屏生命周期

`ScreenShareManager` 保留原公开方法：设置模式与配置 → `initialize()` → 等待 `onInitialized()` → 在前台 Activity 调用 `requestScreenCapture(activity)`。Activity 的 `onActivityResult` 转发给 Manager；其它 requestCode 被忽略。

授权成功后 SDK 启动 `mediaProjection` 前台服务，服务成功进入前台才回调 `onPermissionGranted()`；在该回调中调用 `startShare(url)`（WEBRTC 模式忽略 url），或兼容方法 `startWebRtcShare(resultIntent)`。兼容方法使用 SDK 保存的当前授权结果，不允许注入或重放其它 Intent。SDK 不提前调用 `getMediaProjection`，授权结果只交给 `ScreenCapturerAndroid` 消费一次。

重复 request 报 `BUSY`；重复 start 在当前采集期间忽略。拒绝不启动服务；服务启动失败报告 `FOREGROUND_SERVICE_ERROR`。`stopShare()` 清除授权并停止采集和服务，再次启动必须重新授权。授权弹窗期间 stop 后须先接收并丢弃该次结果，才能重新请求，避免固定 requestCode 的迟到结果串入下一会话。`destroy()` 幂等且终结该实例；需要重新创建 Manager。初始化后请勿变更模式。

`BbbSdpProxyManager` 的 `initializeScreenShare` JS 请求同样在授权后等服务就绪再创建 capturer；拒绝、取消和服务错误会拒绝对应 JS 请求，重复请求不会覆盖原 sequence。`stopScreenShare()`/`release()` 释放采集与服务，release 后重新创建实例。两个入口共享一个前台服务，同时只能有一个服务拥有者；并发入口会明确失败。请将生命周期调用与授权结果在主线程串行转发，并在宿主销毁时释放。

RTMP 入口目前只有屏幕采集，没有 RTMP 编码/推流实现。

## 与 IM 组合

引入 IM 仓库的 `im-call` 即可在聊天中发起/接听通话，邀请通过 IM 消息发送。
## 混淆

官方 Demo 的 release 构建未开启混淆（`minifyEnabled false`），SDK 暂未随包提供 consumer 混淆规则。宿主如需开启混淆：先在未混淆配置下完成功能验证，再逐步收紧并补充 keep 规则（重点：`com.oort.**`、路由注解生成类、XMPP 协议实体、各第三方 SDK 官方规则）。

## 常见问题

| 现象 | 原因与处理 |
|---|---|
| 第三方依赖解析失败 | repositories 未配齐 jitpack / aliyun-jcenter / 华为仓库，按第一步完整清单补齐 |
| 安装失败（签名冲突） | Demo APK 为开发签名，与正式包签名不同，混装需先卸载 |
| 模拟器上运行/登录闪退 | 原生库仅 arm64-v8a，x86 系模拟器不受支持，请使用 arm64 真机 |
| 登录提示"无法连接服务器" | 确认当前网络可访问 `oa.oortcloudsmart.com:2443` |
