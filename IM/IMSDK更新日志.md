# IMSDK 更新日志

## v1.0.0（2026-07-10）

首次正式发布。

### 新增功能

**核心架构**

- 基于 XMPP 协议（Smack 4.4.6）的即时通讯能力
- CoreManager 门面类封装，统一管理服务绑定、消息收发、群聊等全部操作
- CoreStatusListener 回调接口，支持服务就绪状态通知
- 两种初始化模式：简单实例化（getInstance）与带回调初始化（init + onCoreReady）
- 自动重连机制：autoReconnect() 静默重连与 autoReconnectShowProgress() 带提示重连
- packetId 自动生成，CoreManager 内部自动为未设置的消息填充唯一标识

**单聊**

- 支持文本、图片、语音、视频、位置、文件、名片等消息类型
- 消息回执：已读回执功能，支持单聊和群聊（通过广播 OtherBroadcast.Read）
- 消息撤回：通过 TYPE_BACK (202) 消息类型实现
- 红包与转账消息支持

**群聊**

- 创建/加入/退出群组
- 群消息收发（sendMucChatMessage）
- 群内昵称修改
- 禁言管理（支持禁言时长与解除）
- 群组删除、踢出群等事件回调

**好友管理**

- 添加/删除好友，黑名单，关注/取关
- 备注名管理
- 好友请求发送与监听（NewFriendListener）
- 本地好友关系持久化（FriendDao）

**消息加密与安全**

- 支持 3DES / AES / DH+AES 多种加密方式
- 加密类型由好友关系 encryptType 字段决定（0=明文, 1=3DES, 2=AES, 3=DH+AES）
- 消息过期：支持消息自动销毁时间配置（deleteTime / chatRecordTimeOut）

**多点登录与离线**

- 多设备在线状态同步（TYPE_SEND_ONLINE_STATUS 200 协议）
- 离线消息拉取与消息漫游
- 离线时间自动记录与同步

**数据持久化**

- 基于 SQLite 的本地数据存储
- FriendDao：好友/联系人表操作
- ChatMessageDao：聊天消息表操作
- NewFriendDao：新朋友消息表操作
- RoomMemberDao：群成员表操作
- ContactDao：手机联系人表操作

**服务与通知**

- CoreService 以 Foreground Service 运行，保障消息推送稳定性
- 通知栏消息提醒，根据消息类型展示不同通知内容
- ListenerManager 全局监听器管理（连接状态、消息、好友请求、群聊事件）

**集成方式**

- 单 AAR 封装：所有功能封装在 `omm-lib` 中
- 支持 JitPack、Nexus、GitLab、本地 AAR 四种依赖方式

### 已知限制

- 存储权限和安装权限需宿主 App 在运行时主动申请
- Android 8.0+ 需使用 `ContextCompat.startForegroundService()` 启动 IM 服务（CoreManager.init() 内部已处理）
- 消息加密类型由好友关系中的 `encryptType` 字段决定，暂不支持动态切换
- 必须在 REST API 登录成功后再初始化 CoreManager

---

**文档版本**: v1.0.0
**发布日期**: 2026-07-10
**适用 SDK 版本**: omm-lib 1.0.0+
