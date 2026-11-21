# 应用市场 SDK（2.0.0-rc.20261117.3）

> 状态：**RC 集成候选**（RC3，2026-11-17 构建），非正式发布版。

## 组成

| 模块 | Maven 坐标 | 位置 |
|---|---|---|
| 应用市场 | `com.oort.workup:appstore:2.0.0-rc.20261117.3` | `../maven-repo/com/oort/workup/appstore/` |

## 集成方式

模块化发布：`appstore` 依赖底座模块（`base`/`core`/`frame`/`capabilities` 等），全部 AAR 与 POM 已包含在 [`../maven-repo/`](../maven-repo/)，可直接作为 file-based Maven 仓库使用：

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    implementation "com.oort.workup:appstore:2.0.0-rc.20261117.3"
}
```

集成细节见 `docs/应用市场集成指南.md`。

## Demo

`../apks/workup-appstore-sdk-demo.apk`（开发签名，仅验收用）。
