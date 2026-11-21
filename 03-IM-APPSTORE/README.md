# IM + 应用市场 组合 SDK（2.0.0-rc.20261117.3）

> 状态：**RC 集成候选**（RC3，2026-11-17 构建），非正式发布版。

## 组成

| 模块 | Maven 坐标 |
|---|---|
| IM 即时通讯 | `com.oort.workup:im:2.0.0-rc.20261117.3` |
| IM 通话桥接 | `com.oort.workup:im-call:2.0.0-rc.20261117.3` |
| 应用市场 | `com.oort.workup:appstore:2.0.0-rc.20261117.3` |

对应 AAR 均在 [`../maven-repo/`](../maven-repo/)（含全部底座依赖：`base`/`core`/`frame`/`capabilities`/`auth` 等），可直接作为 file-based Maven 仓库使用：

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:appstore:2.0.0-rc.20261117.3"
}
```

集成细节见 `docs/` 下 IM 与应用市场两份集成指南。

## Demo

本组合无独立 demo APK；单 SDK demo 见 `../apks/workup-im-sdk-demo.apk` 与 `../apks/workup-appstore-sdk-demo.apk`。完整组合宿主（OA2443）因体积超过 GitHub 单文件 100MB 限制，未随仓库分发。
