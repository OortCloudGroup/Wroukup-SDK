# 首页 + IM + 应用市场 + 音视频 SDK 集成指南

> 组合包，先读门户首页说明，再依次阅读 IM、应用市场、音视频三份集成指南。

# workup-portal-sdk

由单仓库 `workup-sdk` 拆分而来，源提交 `2aee1ad1`。

## 目录

- `sdk/`：库模块，每个子目录发布为一个 artifact
- `demo/`：独立 demo，只依赖本仓库的 SDK 与底座，演示内容见 `demo/README.md`
- `docs/`：集成指南、更新日志；`docs/archive/` 为拆分前的历史文档
- `.gitlab-ci.yml`：编译 SDK → 编译 demo APK → 打 tag 时发布到内网 Nexus

| 目录 | 发布坐标 | 原单仓库路径 |
|---|---|---|
| `sdk/portal/` | `com.oort.workup:portal` | `features/portal` |
| `demo/` | 应用（不发布） | `demos/portal-demo` |

版本号见 `gradle.properties` 的 `workupVersion`。集成方法见 `docs/集成指南.md`。

## 依赖的其它 WorkUp 仓库

- `com.oort.workup:core`（来自 `workup-foundation`）
- `com.oort.workup:router-compiler`（来自 `workup-foundation`）

## 构建

```bash
./gradlew assembleRelease          # 编译
./gradlew publish -Pnexus          # 发布到内网 Nexus（需 nexusUsername / nexusPassword）
```

Nexus 账号放在 `~/.gradle/gradle.properties` 或环境变量 `NEXUS_USERNAME` / `NEXUS_PASSWORD`，不要提交到仓库。

## 本地源码联调

依赖的仓库尚未发布到 Nexus，或需要跨仓库同时改代码时，把它们克隆到同级目录，
并在本仓库 `local.properties` 中加入：

```properties
workup.includeBuilds=../workup-foundation
```

Gradle 会用本地源码工程替换对应的 Maven 依赖。

## 状态

- 工程骨架由拆分脚本生成。各仓库在 2026-11-14 的基线上验证过：demo 单独可编译，全部仓库可组合出完整 App。
- 未做真机验证。

---

# IM SDK 集成指南

即时通讯：单聊、群聊、会话、好友、朋友圈、短视频、直播等。

| artifact | 内容 |
|---|---|
| `im` | IM 本体，只依赖底座 |
| `im-call` | IM 与音视频 SDK 的桥接：聊天中发起/接听通话（依赖 `im` + 音视频 SDK） |

## 依赖

```groovy
// settings.gradle 或根 build.gradle 的 repositories 中
maven {
    url = "http://192.168.88.218:8403/repository/maven-snapshots/"   // 快照版
    allowInsecureProtocol = true
}
maven {
    url = "http://192.168.88.218:8403/repository/workup-sdk-releases/" // 正式版
    allowInsecureProtocol = true
}
```

```groovy
dependencies {
    implementation "com.oort.workup:im:2.0.0-SNAPSHOT"
}
```

需要在聊天中通话时再加上 `im-call`（会带上音视频 SDK）：

```groovy
dependencies {
    implementation "com.oort.workup:im:2.0.0-SNAPSHOT"
    implementation "com.oort.workup:im-call:2.0.0-SNAPSHOT"
}
```

## 初始化

Application 继承 `com.oort.weichat.MyApplication`。IM 的其余初始化由 `ImInitProvider` 自动完成。

## 主界面

IM 按以下顺序决定"主界面"：

1. 宿主调用 `ImHost.setMainActivityClass(...)` 指定的页面；
2. 集成了门户外壳时为门户主界面（IM 会注册"消息""好友"页签，并在门户主界面出现时建立长连接）；
3. 集成了办公整合包时为其 `MainActivity`；
4. 都没有时为 IM 自带的 `ImMainActivity`（消息、好友两个页签）。

## 与其它 SDK 组合

| 组合 | 效果 |
|---|---|
| + 通讯录 SDK | 点头像打开组织人员详情；建群、群内加人使用组织选人；头像优先取组织头像 |
| + 音视频 SDK + `im-call` | 聊天中发起单聊通话、群会议；接听来电 |
| + 应用市场 SDK | 登录后预加载应用列表 |
| + 门户 | 以门户页签形式出现 |

未组合时的降级：人员详情用 IM 好友资料页；建群用 IM 好友选择页；群内加人用 IM 邀请好友页；发起通话提示"未集成音视频组件"；收到通话邀请只做提示。

## 宿主可提供的能力

`ImHost.Hooks`（`ImHost.setHooks`）：主界面状态、H5 首页预加载、离线登录、动态/新闻页跳转等，默认均为空实现。

## demo

`demo/` 只依赖 IM：登录后进入 IM 自带主界面，可收发消息、建群、群内加人、查看好友资料。

## 历史文档

`archive/` 中为拆分前的 IM SDK 集成指南、功能清单、交付文档，描述的是旧的单 AAR 集成方式，仅供参考。

---

# 应用市场 SDK 集成指南

企业应用市场：应用展示、分类、下载、安装、更新、静默更新、工作台。

## 依赖

```groovy
// settings.gradle 或根 build.gradle 的 repositories 中
maven {
    url = "http://192.168.88.218:8403/repository/maven-snapshots/"   // 快照版
    allowInsecureProtocol = true
}
maven {
    url = "http://192.168.88.218:8403/repository/workup-sdk-releases/" // 正式版
    allowInsecureProtocol = true
}
```

```groovy
dependencies {
    implementation "com.oort.workup:appstore:2.0.0-SNAPSHOT"
}
```

## 初始化

无需手工初始化。`AppStoreInitProvider` 在启动时设置上下文、注册 `IAppInfoProvider`，并向门户注册"工作台"页签。

登录后调用 `AppStoreContext.initialize(token, uuid)` 设置登录态（集成 IM 时由 IM 完成）。

## 使用

| 能力 | 方式 |
|---|---|
| 打开应用市场 | `startActivity(new Intent(ctx, AppStoreActivity.class))`，或路由 `appstore/home` |
| 打开应用 | 路由 `appstore/open`，参数 `packageName`、`params` |
| 应用信息、应用列表 | `ServiceLocator.get(IAppInfoProvider.class)` |
| 承载 H5 应用的页面 | 实现 `IAppOpenDelegate` 并 `setDelegate` |

H5 类应用需要同时集成 H5 应用底座 SDK 才能打开。

## demo

`demo/` 只依赖应用市场 SDK 与底座：应用市场、工作台页签、加载应用列表、按包名查询、按包名打开应用。

## 历史文档

`archive/` 中为拆分前的集成指南与更新日志。

---

# 音视频 SDK 集成指南

| artifact | 内容 |
|---|---|
| `av-call` | 音视频通话与会议（MiroTalk） |
| `av-screen-share` | 屏幕共享（WebRTC 采集 + SDP 代理） |

不依赖 IM，也不依赖底座的其它模块（`av-call` 只依赖 AndroidX、OkHttp）。

## 依赖

```groovy
// settings.gradle 或根 build.gradle 的 repositories 中
maven {
    url = "http://192.168.88.218:8403/repository/maven-snapshots/"   // 快照版
    allowInsecureProtocol = true
}
maven {
    url = "http://192.168.88.218:8403/repository/workup-sdk-releases/" // 正式版
    allowInsecureProtocol = true
}
```

```groovy
dependencies {
    implementation "com.oort.workup:av-call:2.0.0-SNAPSHOT"
    implementation "com.oort.workup:av-screen-share:2.0.0-SNAPSHOT"
}
```

两者对 WebRTC 保留 `compileOnly` 契约，不由 AAR/POM 传递该依赖。公开接口含 `org.webrtc.SessionDescription`、`IceCandidate` 等类型，因此使用方必须同时提供编译 classpath 和运行时 WebRTC（包括 RTMP 命名入口，其采集实现也使用 WebRTC）。当前匹配版本如下，未在此次修复中升级：

```groovy
implementation "io.github.webrtc-sdk:android:125.6422.07"
```

中间封装库若继续公开这些类型，应使用 `api "io.github.webrtc-sdk:android:125.6422.07"`；最终应用使用 `implementation`。不要同时引入旧版 `org.webrtc:google-webrtc` 或另一套含相同 `org.webrtc.*` 类/native 库的产物。demo 已显式声明此依赖。实际设备/ABI 和 16KB 页兼容性仍须对最终 APK 验证，不能仅由版本号推断。

## 独立投屏生命周期

`ScreenShareManager` 保留原公开方法：设置模式与配置 → `initialize()` → 等待 `onInitialized()` → 在前台 Activity 调用 `requestScreenCapture(activity)`。Activity 的 `onActivityResult` 转发给 Manager；其它 requestCode 被忽略。

授权成功后 SDK 启动 `mediaProjection` 前台服务，服务成功进入前台才回调 `onPermissionGranted()`；在该回调中调用 `startShare(url)`（WEBRTC 模式忽略 url），或兼容方法 `startWebRtcShare(resultIntent)`。兼容方法使用 SDK 保存的当前授权结果，不允许注入或重放其它 Intent。SDK 不提前调用 `getMediaProjection`，授权结果只交给 `ScreenCapturerAndroid` 消费一次。

重复 request 报 `BUSY`；重复 start 在当前采集期间忽略。拒绝不启动服务；服务启动失败报告 `FOREGROUND_SERVICE_ERROR`。`stopShare()` 清除授权并停止采集和服务，再次启动必须重新授权。授权弹窗期间 stop 后须先接收并丢弃该次结果，才能重新请求，避免固定 requestCode 的迟到结果串入下一会话。`destroy()` 幂等且终结该实例；需要重新创建 Manager。初始化后请勿变更模式。

`BbbSdpProxyManager` 的 `initializeScreenShare` JS 请求同样在授权后等服务就绪再创建 capturer；拒绝、取消和服务错误会拒绝对应 JS 请求，重复请求不会覆盖原 sequence。`stopScreenShare()`/`release()` 释放采集与服务，release 后重新创建实例。两个入口共享一个前台服务，同时只能有一个服务拥有者；并发入口会明确失败。请将生命周期调用与授权结果在主线程串行转发，并在宿主销毁时释放。

RTMP 入口目前只有屏幕采集，没有 RTMP 编码/推流实现；本次修复不改变此能力边界。

## 使用

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

## 与 IM 组合

引入 IM 仓库的 `im-call` 即可在聊天中发起/接听通话，邀请通过 IM 消息发送。

## demo

`demo/` 只依赖音视频 SDK 与底座（登录），不需要 IM：发起视频/语音会议、按会议号加入会议、会议内共享屏幕。

## 历史文档

`archive/` 中为 call-sdk、screen-share 原有的说明与接口文档。
