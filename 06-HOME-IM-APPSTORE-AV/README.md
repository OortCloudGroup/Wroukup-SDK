# 首页 + IM + 应用市场 + 音视频 组合 SDK（2.0.0-rc.20261117.3）

> 状态：**RC 集成候选**（RC3，2026-11-17 构建），非正式发布版。这是最接近完整 App 形态的组合。

## 组成

| 模块 | Maven 坐标 |
|---|---|
| 门户首页（主界面框架/底部导航/首页/我的） | `com.oort.workup:portal:2.0.0-rc.20261117.3` |
| IM 即时通讯 | `com.oort.workup:im:2.0.0-rc.20261117.3` |
| IM 通话桥接 | `com.oort.workup:im-call:2.0.0-rc.20261117.3` |
| 应用市场 | `com.oort.workup:appstore:2.0.0-rc.20261117.3` |
| 音视频通话 | `com.oort.workup:av-call:2.0.0-rc.20261117.3` |
| 屏幕共享 | `com.oort.workup:av-screen-share:2.0.0-rc.20261117.3` |

对应 AAR 均在 [`../maven-repo/`](../maven-repo/)（含全部底座依赖），可直接作为 file-based Maven 仓库使用。音视频模块还需宿主引入 `io.github.webrtc-sdk:android:125.6422.07`。

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    implementation "com.oort.workup:portal:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:appstore:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:av-call:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:av-screen-share:2.0.0-rc.20261117.3"
    implementation "io.github.webrtc-sdk:android:125.6422.07"
}
```

集成细节见 `docs/` 下各份文档。

## Demo

门户单 SDK demo：`../apks/workup-portal-sdk-demo.apk`；其余单 SDK demo 同目录。完整组合宿主（OA2443）因超过 GitHub 单文件 100MB 限制，未随仓库分发。
