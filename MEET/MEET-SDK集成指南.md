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
