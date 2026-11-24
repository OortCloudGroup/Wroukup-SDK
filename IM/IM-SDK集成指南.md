# IM SDK 集成指南

即时通讯：单聊、群聊、会话、好友、朋友圈、短视频、直播等。

| artifact | 内容 |
|---|---|
| `im` | IM 本体，只依赖底座 |
| `im-call` | IM 与音视频 SDK 的桥接：聊天中发起/接听通话（依赖 `im` + 音视频 SDK） |

## 第一步：引入依赖（离线依赖包）

1. 下载本卡片目录 `IM-SDK/` 下的 `IM-SDK-maven-repo.zip` 并解压，得到 `maven-repo/` 离线仓库（含 com.oort 全部模块 AAR+POM 与内部依赖闭包）；
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


3. 引入坐标（完整坐标清单见 `IM-SDK/README.md`）：

```groovy
dependencies {
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
    // 需要在聊天中发起/接听通话时再加（会带上音视频 SDK）：
    // implementation "com.oort.workup:im-call:2.0.0-rc.20261117.3"
}
```

## 第二步：初始化

Application 继承 IM 的 `com.oort.weichat.MyApplication`（它已完成底座登录所需的全部初始化，外加 IM 自己的初始化，IM 其余初始化由 `ImInitProvider` 自动完成）。官方 Demo 的写法：

```java
public class ImDemoApplication extends MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ProjectConfig.init(BuildConfig.PROJECT_PRESET);   // 环境预设，默认 OA_2443
        Constant.ENABLE_IM = true;
        Constant.ENABLE_PATROL = false;
        UserProviderHolder.setProvider(new LoginUserProvider());
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
   - 未集成门户时由 SDK 自带主界面承接（IM 卡片为 `ImMainActivity`，已通过 manifest merge 自动注册）。

3. 服务器环境：默认预设 **OA_2443**，对应网关 `https://oa.oortcloudsmart.com:2443/`（业务、IM 接口走单端口 HTTPS 网关，消息长连接走 WSS）。私有化部署时按 `ProjectConfig` 中的预设模板配置自有域名。

4. 运行时权限：SDK 的 manifest 已合并全部权限声明，宿主**无需手工添加**；以下敏感权限由登录模块在启动页统一弹窗申请——消息通知、定位、麦克风、相机、媒体读写（Android 13+ 为 `READ_MEDIA_*`）。

5. 体验账号：`18948726601` / `12345678`（详见仓库根 README 与本卡片 `IM-SDK-Demo/README.md` 的「体验账号」板块，2026-11-23 实机验证：登录、消息收发、通讯录均正常）。

## 主界面

IM 按以下顺序决定"主界面"：

1. 宿主调用 `ImHost.setMainActivityClass(...)` 指定的页面；
2. 集成了门户外壳时为门户主界面（IM 会注册"消息""好友"页签，并在门户主界面出现时建立长连接）；
3. 集成了办公整合包时为其 `MainActivity`；
4. 都没有时为 IM 自带的 `ImMainActivity`（消息、好友两个页签）。

宿主可提供的能力：`ImHost.Hooks`（`ImHost.setHooks`）——主界面状态、H5 首页预加载、离线登录、动态/新闻页跳转等，默认均为空实现。

## 与其它 SDK 组合

| 组合 | 效果 |
|---|---|
| + 通讯录 SDK | 点头像打开组织人员详情；建群、群内加人使用组织选人；头像优先取组织头像 |
| + 音视频 SDK + `im-call` | 聊天中发起单聊通话、群会议；接听来电 |
| + 应用市场 SDK | 登录后预加载应用列表 |
| + 门户 | 以门户页签形式出现 |

未组合时的降级：人员详情用 IM 好友资料页；建群用 IM 好友选择页；群内加人用 IM 邀请好友页；发起通话提示"未集成音视频组件"；收到通话邀请只做提示。

## 混淆

官方 Demo 的 release 构建未开启混淆（`minifyEnabled false`），SDK 暂未随包提供 consumer 混淆规则。宿主如需开启混淆：先在未混淆配置下完成功能验证，再逐步收紧并补充 keep 规则（重点：`com.oort.**`、路由注解生成类、XMPP 协议实体、各第三方 SDK 官方规则）。

## 常见问题

| 现象 | 原因与处理 |
|---|---|
| 第三方依赖解析失败 | repositories 未配齐 jitpack / aliyun-jcenter / 华为仓库，按第一步完整清单补齐 |
| 安装失败（签名冲突） | Demo APK 为开发签名，与正式包签名不同，混装需先卸载 |
| 模拟器上运行/登录闪退 | 原生库仅 arm64-v8a，x86 系模拟器不受支持，请使用 arm64 真机 |
| 登录提示"无法连接服务器" | 确认当前网络可访问 `oa.oortcloudsmart.com:2443` |
