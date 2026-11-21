# WorkUp-SDK

> **版本**: `2.0.0-rc.20261117.3`（RC 集成候选，2026-11-17 构建）
> **更新**: 2026-11-21 重构为官网 6 卡片结构，每卡片提供开箱即用依赖包
> **状态**: 可用于集成与验收；正式发布阻塞项（16KB 兼容、RTMP 串流、离线推送等）见交付文档

对应官网下载页 6 张卡片，每个卡片一个目录，内含 4 项：

| 卡片目录 | SDK 依赖包（开箱即用） | Demo |
|---|---|---|
| [`IM/`](IM/) | `IM-SDK/IM-SDK-maven-repo.zip`（72.7MB） | `IM-SDK-Demo/workup-im-sdk-demo.apk` |
| [`MPAAS-APPSTORE/`](MPAAS-APPSTORE/) | `MPAAS-APPSTORE-SDK/…-maven-repo.zip`（5.3MB） | `…-SDK-Demo/workup-appstore-sdk-demo.apk` |
| [`IM-MPAAS-APPSTORE/`](IM-MPAAS-APPSTORE/) | `…-SDK/…-maven-repo.zip`（75.7MB） | `…-SDK-Demo/`（im + appstore 两个 demo） |
| [`MEET/`](MEET/) | `MEET-SDK/MEET-SDK-maven-repo.zip`（0.2MB） | `MEET-SDK-Demo/workup-av-sdk-demo.apk` |
| [`IM-MPAAS-APPSTORE-MEET/`](IM-MPAAS-APPSTORE-MEET/) | `…-SDK/…-maven-repo.zip`（75.7MB） | `…-SDK-Demo/`（im + appstore + av 三个 demo） |
| [`Portal-IM-MPAAS-APPSTORE-MEET/`](Portal-IM-MPAAS-APPSTORE-MEET/) | `…-SDK/…-maven-repo.zip`（75.8MB） | `…-SDK-Demo/`（含 portal 共四个 demo） |

每个卡片目录结构统一：

```
<卡片>/
├── <卡片>-SDK/                  # <卡片>-SDK-maven-repo.zip（依赖闭包）+ 直接 AAR + README
├── <卡片>-SDK-Demo/             # 编译好的 demo APK（开发签名，仅验收用）
├── <卡片>-SDK集成指南.md
└── <卡片>-SDK更新日志.md
```

## 快速集成（三步）

1. 下载卡片目录里的 `<卡片>-SDK-maven-repo.zip` 并解压
2. Gradle 指向解压出的仓库：

```groovy
repositories {
    maven { url = uri('/解压路径/maven-repo') }
    google()
    mavenCentral()
}
dependencies {
    // 按卡片引入坐标，例如 IM：
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
}
```

3. 各卡片的坐标清单见 `<卡片>-SDK/README.md`；音视频相关卡片需宿主额外引入 `io.github.webrtc-sdk:android:125.6422.07`。

## 说明

- 每个 `-maven-repo.zip` 内含该组合的**完整内部依赖闭包**（base/core/frame/capabilities/auth/router/rtmp 等 AAR+POM 全部打包），解压即用、离线可用；第三方公开依赖（AndroidX、Smack 等）由 POM 声明、从 google()/mavenCentral() 解析。
- [`maven-repo/`](maven-repo/) 是全量 file-based 制品库（覆盖全部 6 种组合的所有模块），整仓 clone 的用户可直接指向它。
- demo APK 为开发签名，仅供安装验收，不能覆盖签名不同的正式包。
- 完整组合宿主 APK（OA2443，199MB）超过 GitHub 单文件 100MB 限制，未随仓库分发。
- RC 集成候选 ≠ 正式发布版。
