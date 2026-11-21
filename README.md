# WorkUp-SDK

> **版本**: `2.0.0-rc.20261117.3`（RC 集成候选，2026-11-17 构建）
> **更新**: 2026-11-21 全量替换旧版（v1.x 目录已移除）
> **状态**: 可用于集成与验收；正式发布阻塞项（16KB 兼容、RTMP 串流、离线推送等）见交付文档

对应官网下载页 6 张卡片的**模块化 SDK**，目录结构与卡片一一对应：

| 目录 | 卡片 |
|---|---|
| [`01-IM/`](01-IM/) | IM |
| [`02-APPSTORE/`](02-APPSTORE/) | 应用市场 |
| [`03-IM-APPSTORE/`](03-IM-APPSTORE/) | IM + 应用市场 |
| [`04-AV/`](04-AV/) | 音视频 |
| [`05-IM-APPSTORE-AV/`](05-IM-APPSTORE-AV/) | IM + 应用市场 + 音视频 |
| [`06-HOME-IM-APPSTORE-AV/`](06-HOME-IM-APPSTORE-AV/) | 首页 + IM + 应用市场 + 音视频 |

每个卡片目录包含：

- `README.md` — 组成模块、Maven 坐标、依赖写法
- `docs/` — 集成指南、更新日志（来自各 SDK 仓库）

公共资源：

| 目录 | 说明 |
|---|---|
| [`maven-repo/`](maven-repo/) | 完整 file-based Maven 仓库：全部模块 AAR + POM（`com.oort.workup:*:2.0.0-rc.20261117.3`） |
| [`apks/`](apks/) | 6 个单 SDK demo APK（开发签名，仅验收用） |

## 快速集成

```groovy
repositories {
    maven { url = uri('/path/to/Wroukup-SDK/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    // 按所选卡片引入对应坐标，例如 IM + 应用市场：
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:appstore:2.0.0-rc.20261117.3"
}
```

- 底座依赖（`base`/`core`/`frame`/`capabilities`/`auth` 等）由 `maven-repo/` 的 POM 自动解析，无需手动引入。
- 音视频卡片需宿主额外引入 `io.github.webrtc-sdk:android:125.6422.07`（`av-call`/`av-screen-share` 对 WebRTC 为 `compileOnly`）。
- 各卡片 README 列出了该组合的完整坐标清单。

## 注意事项

- 本版为**模块化发布**，区别于旧版单文件聚合 AAR（`omm-lib`）。
- demo APK 为开发签名，仅供安装验收，不能覆盖签名不同的正式包。
- 完整组合宿主 APK（OA2443，199MB）超过 GitHub 单文件 100MB 限制，未随仓库分发。
- RC 集成候选 ≠ 正式发布版，正式发布阻塞项见交付文档。
