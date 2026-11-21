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
