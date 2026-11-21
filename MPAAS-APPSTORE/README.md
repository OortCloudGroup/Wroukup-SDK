# 应用市场 SDK（2.0.0-rc.20261117.3）

> 对应官网下载页「MPAAS-APPSTORE」卡片。RC 集成候选，非正式发布版。

## 目录内容

| 内容 | 位置 |
|---|---|
| SDK 依赖包（开箱即用，含完整依赖闭包） | [`MPAAS-APPSTORE-SDK/MPAAS-APPSTORE-SDK-maven-repo.zip`](MPAAS-APPSTORE-SDK/) |
| 模块 AAR 与 Maven 坐标说明 | [`MPAAS-APPSTORE-SDK/README.md`](MPAAS-APPSTORE-SDK/README.md) |
| 编译好的 Demo APK（开发签名，仅验收用） | [`MPAAS-APPSTORE-SDK-Demo/`](MPAAS-APPSTORE-SDK-Demo/) —— `workup-appstore-sdk-demo.apk` |
| 集成指南 | [`MPAAS-APPSTORE-SDK集成指南.md`](MPAAS-APPSTORE-SDK集成指南.md) |
| 更新日志 | [`MPAAS-APPSTORE-SDK更新日志.md`](MPAAS-APPSTORE-SDK更新日志.md) |

## 界面预览（Demo 实机截图，含登录态验收实拍）

<table><tr><td><img src="../screenshots/appstore-menu.png" width="220"/><br/><sub>Demo 功能菜单</sub></td><td><img src="../screenshots/appstore-home.png" width="220"/><br/><sub>应用市场首页</sub></td><td><img src="../screenshots/appstore-category.png" width="220"/><br/><sub>分类页</sub></td><td><img src="../screenshots/appstore-detail.png" width="220"/><br/><sub>应用详情</sub></td></tr></table>

> 会话、会议等在线功能需登录服务器账号后体验。
> 快速集成三步：下载并解压 zip → Gradle 指向其中 `maven-repo/` → 按坐标引入，详见上方集成指南。
