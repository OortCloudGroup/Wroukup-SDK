# 应用市场 SDK 集成指南

企业应用市场：应用展示、分类、下载、安装、更新、静默更新、工作台。

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
    implementation "com.oort.workup:appstore:2.0.0-SNAPSHOT"
}
```

## 初始化

无需手工初始化。`AppStoreInitProvider` 在启动时设置上下文、注册 `IAppInfoProvider`，并向门户注册"工作台"页签。

登录后调用 `AppStoreContext.initialize(token, uuid)` 设置登录态（集成 IM 时由 IM 完成）。

## 使用

| 能力 | 方式 |
|---|---|
| 打开应用市场 | `startActivity(new Intent(ctx, AppStoreActivity.class))`，或路由 `appstore/home` |
| 打开应用 | 路由 `appstore/open`，参数 `packageName`、`params` |
| 应用信息、应用列表 | `ServiceLocator.get(IAppInfoProvider.class)` |
| 承载 H5 应用的页面 | 实现 `IAppOpenDelegate` 并 `setDelegate` |

H5 类应用需要同时集成 H5 应用底座 SDK 才能打开。

## demo

`demo/` 只依赖应用市场 SDK 与底座：应用市场、工作台页签、加载应用列表、按包名查询、按包名打开应用。

## 历史文档

`archive/` 中为拆分前的集成指南与更新日志。
