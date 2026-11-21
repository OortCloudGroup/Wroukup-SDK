# 音视频 SDK（2.0.0-rc.20261117.3）

> 状态：**RC 集成候选**（RC3，2026-11-17 构建），非正式发布版。

## 组成

| 模块 | Maven 坐标 | 位置 |
|---|---|---|
| 音视频通话 | `com.oort.workup:av-call:2.0.0-rc.20261117.3` | `../maven-repo/com/oort/workup/av-call/` |
| 屏幕共享 | `com.oort.workup:av-screen-share:2.0.0-rc.20261117.3` | `../maven-repo/com/oort/workup/av-screen-share/` |

**注意**：两个模块对 WebRTC 为 `compileOnly`，宿主必须自行引入 `io.github.webrtc-sdk:android:125.6422.07`（详见集成指南）。

## 集成方式

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    implementation "com.oort.workup:av-call:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:av-screen-share:2.0.0-rc.20261117.3"
    implementation "io.github.webrtc-sdk:android:125.6422.07"
}
```

集成细节见 `docs/音视频集成指南.md`。

## Demo

`../apks/workup-av-sdk-demo.apk`（开发签名，仅验收用）。
