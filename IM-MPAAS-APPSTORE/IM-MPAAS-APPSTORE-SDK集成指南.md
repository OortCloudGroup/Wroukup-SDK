# IM + 应用市场 SDK 集成指南

即时通讯 + 企业应用市场组合包：单聊、群聊、会话、好友 + 应用展示、分类、下载、安装、更新、工作台。

| artifact | 内容 |
|---|---|
| `im` | IM 本体 |
| `appstore` | 应用市场本体 |

## 第一步：引入依赖（离线依赖包）

1. 下载本卡片目录 `IM-MPAAS-APPSTORE-SDK/` 下的 `IM-MPAAS-APPSTORE-SDK-maven-repo.zip` 并解压，得到 `maven-repo/` 离线仓库（含 com.oort 全部模块 AAR+POM 与内部依赖闭包）；
2. 在 `settings.gradle`（或根 `build.gradle`）中配置**完整**仓库清单：

```groovy
repositories {
    maven { url = uri('/解压路径/maven-repo') }   // com.oort 全部模块 + 内部依赖闭包，离线可用
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }           // swipeback / Luban / MPAndroidChart 等
    maven { url 'https://maven.aliyun.com/repository/jcenter' }   // jcenter 归档库
    maven { url 'https://developer.huawei.com/repo/' }            // 华为 HMS 推送
    maven { url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases" }
}
```

> **仓库清单必须完整**：缺 jitpack / aliyun-jcenter / 华为仓库会导致第三方依赖解析失败（已实测按此清单 289 个制品全部解析通过）。


3. 引入坐标（完整坐标清单见 `IM-MPAAS-APPSTORE-SDK/README.md`）：

```groovy
dependencies {
    implementation "com.oort.workup:im:2.0.0-rc.20261117.3"
    implementation "com.oort.workup:appstore:2.0.0-rc.20261117.3"
}
```

## 第二步：初始化

Application 继承 IM 的 `com.oort.weichat.MyApplication`（IM 初始化会同时完成底座、登录与应用市场所需的登录态注入，`AppStoreContext.initialize(token, uuid)` 由 IM 自动完成）：

```java
public class DemoApplication extends MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ProjectConfig.init(BuildConfig.PROJECT_PRESET);   // 环境预设，默认 OA_2443
        Constant.ENABLE_IM = true;
        Constant.ENABLE_PATROL = false;
        UserProviderHolder.setProvider(new LoginUserProvider());
    }
}
```

应用市场无需手工初始化：`AppStoreInitProvider` 在启动时自动注册，并向门户注册"工作台"页签。

## 第三步：登录与主界面

1. 启动页为 SDK 登录模块的 `com.oort.login.common.activity.SplashActivity`，宿主无需再写启动页，直接使用即可：

```xml
<activity
    android:name="com.oort.login.common.activity.SplashActivity"
    android:exported="true"
    android:theme="@style/SplashTheme">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

2. 登录成功后通过隐式跳转 `${applicationId}.main` 进入主界面：
   - 集成门户时由门户主界面承接；
   - 未集成门户时由 SDK 自带主界面承接（IM 卡片为 `ImMainActivity`，已通过 manifest merge 自动注册）。

3. 服务器环境：默认预设 **OA_2443**，对应网关 `https://oa.oortcloudsmart.com:2443/`（业务、IM 接口走单端口 HTTPS 网关，消息长连接走 WSS）。私有化部署时按 `ProjectConfig` 中的预设模板配置自有域名。

4. 运行时权限：SDK 的 manifest 已合并全部权限声明，宿主**无需手工添加**；以下敏感权限由登录模块在启动页统一弹窗申请——消息通知、定位、麦克风、相机、媒体读写（Android 13+ 为 `READ_MEDIA_*`）。

5. 体验账号：`18948726601` / `12345678`（详见仓库根 README 与本卡片 `IM-MPAAS-APPSTORE-SDK-Demo/README.md` 的「体验账号」板块，2026-11-23 实机验证：登录、消息收发、通讯录均正常）。

## 使用要点

- 主界面：IM 自带 `ImMainActivity`（消息、好友页签）通过 `${applicationId}.main` 自动承接，无需额外配置；也可用 `ImHost.setMainActivityClass(...)` 指定宿主页面。
- 打开应用市场：`startActivity(new Intent(ctx, AppStoreActivity.class))`，或路由 `appstore/home`；打开应用走路由 `appstore/open`（参数 `packageName`）。
- H5 类应用需要同时集成 H5 应用底座 SDK 才能打开。
- IM 好友资料/建群等人员能力默认走 IM 自带页面，集成通讯录 SDK 后自动切换为组织选人。
## 混淆

官方 Demo 的 release 构建未开启混淆（`minifyEnabled false`），SDK 暂未随包提供 consumer 混淆规则。宿主如需开启混淆：先在未混淆配置下完成功能验证，再逐步收紧并补充 keep 规则（重点：`com.oort.**`、路由注解生成类、XMPP 协议实体、各第三方 SDK 官方规则）。

## 常见问题

| 现象 | 原因与处理 |
|---|---|
| 第三方依赖解析失败 | repositories 未配齐 jitpack / aliyun-jcenter / 华为仓库，按第一步完整清单补齐 |
| 安装失败（签名冲突） | Demo APK 为开发签名，与正式包签名不同，混装需先卸载 |
| 模拟器上运行/登录闪退 | 原生库仅 arm64-v8a，x86 系模拟器不受支持，请使用 arm64 真机 |
| 登录提示"无法连接服务器" | 确认当前网络可访问 `oa.oortcloudsmart.com:2443` |
