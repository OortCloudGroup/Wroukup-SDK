# 更新日志

## 2.0.0-SNAPSHOT（2026-11-16）

- 修复：只集成 IM 时"添加好友"搜索账号无结果——搜索按钮原地重开输入页；现在打开 IM 用户搜索结果页（点结果进入资料页，可加好友、发消息）
- 新增 im-call 注册底座 `ISingleCallService`（透明中转页绑定 IM 长连接后发起通话），供通讯录人员详情发起语音/视频通话
- IM 独立主界面 `ImMainActivity` 改用底座 `BottomTabBar`（线框/实心图标、选中胶囊、未读角标），"好友"页补标题栏
- 门户页签按 `PortalTab.topBar` 注册（消息沉浸式、好友由门户补标题栏）；"消息"页签显示未读角标；向"我的"注册 IM 入口；门户退出登录时断开长连接

## 2.0.0-SNAPSHOT（2026-11-14）

- IM 可单独使用：不再依赖应用市场、通讯录、H5、音视频、巡逻、语音
- 主界面、底部导航、首页、动态、组织选人、行业定制等整合代码移出 IM
- 新增宿主接口 `ImHost`、`ImCallService`，以及 IM 自带主界面 `ImMainActivity`
- 新增 `im-call`：IM 与音视频 SDK 的通话桥接（原 `CallSdkBridge`、来电页、快速会议页）
- 向门户注册"消息""好友"页签；门户主界面出现时建立长连接
- 厂商推送通道选择独立为 `PushSelector`
- Cordova 插件清单、H5 资源包移出 IM（分别到 H5 底座、办公整合包）
- 通话改为 MiroTalk（并入 `master_BBB_port`）
