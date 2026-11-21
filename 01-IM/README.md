# IM SDK（2.0.0-rc.20261117.3）

> 状态：**RC 集成候选**（RC3，2026-11-17 构建），非正式发布版。正式发布阻塞项见交付 README。

## 组成

| 模块 | Maven 坐标 | 位置 |
|---|---|---|
| IM 即时通讯 | `com.oort.workup:im:2.0.0-rc.20261117.3` | `../maven-repo/com/oort/workup/im/` |
| IM 通话桥接 | `com.oort.workup:im-call:2.0.0-rc.20261117.3` | `../maven-repo/com/oort/workup/im-call/` |

## 集成方式

本版本为**模块化发布**：`im` 依赖底座模块（`base`/`core`/`frame`/`capabilities`/`auth` 等），全部 AAR 与 POM 已包含在 [`../maven-repo/`](../maven-repo/)，可直接作为 file-based Maven 仓库使用：

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
}
```

集成细节见 `docs/IM集成指南.md`。

## Demo

`../apks/workup-im-sdk-demo.apk`（开发签名，仅验收用）。
