# IMSDK 集成指南

## 1. 概述

IMSDK 功能基于 **XMPP 协议**，底层使用 **Smack 4.4.6** 实现长连接通信。所有 IM 相关代码封装在 `omm-lib` 模块中。

### 1.1 核心类说明

| 类名 | 说明 |
|---|---|
| `CoreManager` | **IM 门面类（推荐入口）**，封装服务绑定、消息收发、群聊等全部操作 |
| `CoreStatusListener` | 核心就绪回调接口，`onCoreReady()` 在 XMPP 服务绑定成功后触发 |
| `CoreService` | XMPP 核心后台服务，管理连接生命周期（由 `CoreManager` 内部启动和绑定） |
| `XmppConnectionManager` | XMPP TCP 连接管理，包含登录/登出/重连 |
| `XChatManager` | 单聊消息收发管理 |
| `XMucChatManager` | 群聊消息收发管理 |
| `ListenerManager` | 全局监听器管理（单例） |
| `ReceiptManager` / `ReceiptManagerNew` | 消息回执管理 |

> **说明：** 外部调用方统一通过 `CoreManager` 操作 IM 功能，无需直接与 `CoreService` 交互。`CoreManager` 内部自动管理 `CoreService` 的启动、绑定和消息路由。

### 1.2 核心数据模型

| 类名 | 说明 |
|---|---|
| `ChatMessage` | 聊天消息实体 |
| `NewFriendMessage` | 新朋友/好友请求消息实体 |
| `Friend` | 好友/联系人实体 |
| `User` | 用户实体 |

---

## 2. 集成方式

`omm-lib` 提供了以下四种集成方式。

### 2.1 方式一：GitHub + JitPack（公开仓库）

适用于 GitHub 公开仓库，无需认证。

**1. 添加仓库**

在项目根目录 `build.gradle` 中：

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**2. 添加依赖**

在 App 模块 `build.gradle` 中：

```gradle
dependencies {
    implementation 'com.github.Bean-V.WorkUp-SDK:omm-lib:1.0.0'
}
```

---

### 2.2 方式二：Nexus Maven（企业内网）

适用于企业内部网络，私有部署。

**1. 添加仓库**

```gradle
allprojects {
    repositories {
        maven {
            url = "http://192.168.88.218:8403/repository/workup-sdk-releases/"
            allowInsecureProtocol = true
        }
    }
}
```

**2. 添加依赖**

```gradle
dependencies {
    implementation 'com.oort.workup-sdk:omm-lib:1.0.1'
}
```

---

### 2.3 方式三：GitLab Maven（备用方案）

适用于使用 GitLab Raw URL 访问独立的 Maven Git 仓库。

**1. 添加仓库**

```gradle
allprojects {
    repositories {
        maven {
            url = "http://192.168.88.125/zhangzhijun/workup-sdk-maven/raw/master"
            allowInsecureProtocol = true

            // 如果仓库是私有的，需要添加认证
            credentials(HttpHeaderCredentials) {
                name = "Private-Token"
                value = "your-gitlab-token"
            }

            authentication {
                header(HttpHeaderAuthentication)
            }
        }
    }
}
```

**2. 添加依赖**

```gradle
dependencies {
    implementation 'com.oort.workup-sdk:omm-lib:1.0.1'
}
```

---

### 2.4 方式四：本地 AAR 文件（离线集成）

直接使用 `omm-lib-1.0.0.aar` 文件进行本地集成。

**1. 放置 AAR 文件**

将 `omm-lib-1.0.0.aar` 复制到 App 模块的 `libs/` 目录下。

**2. 添加依赖**

```gradle
// 在 App 模块 build.gradle 中
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation files('libs/omm-lib-1.0.0.aar')
    // 或
    // implementation(name: 'omm-lib-1.0.0', ext: 'aar')
}
```

---

### 所需权限

在 `AndroidManifest.xml` 中需包含以下权限（omm-lib 已声明）：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

---

## 3. IM 集成步骤

### 3.1 初始化 CoreManager

`CoreManager` 提供两种初始化方式：

**方式一：简单初始化（无状态回调）**

适用于已确认用户已登录的场景，直接获取实例即可：

```java
// 获取 CoreManager 单例
CoreManager coreManager = CoreManager.getInstance(context);

// 检查服务是否就绪
if (coreManager.isServiceReady()) {
    // 可以开始发送消息
}

// 检查是否已登录（XMPP 已认证）
if (coreManager.isLogin()) {
    // XMPP 连接已建立
}
```

**方式二：带状态回调初始化（推荐）**

适用于 Activity/Fragment 中首次启动 IM 服务，需要等待服务就绪后再操作：

```java
public class ChatActivity extends AppCompatActivity implements CoreStatusListener {

    private CoreManager coreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建 CoreManager，传入回调
        coreManager = new CoreManager(this, this);
        // 初始化：参数 loginRequired=true 会自动启动 CoreService 并绑定
        coreManager.init(true, false);
    }

    @Override
    public void onCoreReady() {
        // XMPP 服务已绑定成功，可以开始发送消息
        Log.d("Chat", "CoreManager 已就绪");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解绑服务
        if (coreManager != null) {
            coreManager.destroy();
        }
    }
}
```

> **注意：** `init(loginRequired, configRequired)` 参数说明：
> - `loginRequired = true`：要求本地已有用户登录数据，否则会自动跳转登录页
> - `configRequired = true`：同时加载网络配置（API 地址、XMPP 服务器等）

### 3.2 监听连接状态

通过 `ListenerManager` 注册连接状态监听：

```java
// 注册认证状态监听
ListenerManager.getInstance().addAuthStateChangeListener(new AuthStateListener() {
    @Override
    public void onAuthStateChange(int authState) {
        switch (authState) {
            case AuthStateListener.AUTH_STATE_NOT:      // 1 - 未登录
                break;
            case AuthStateListener.AUTH_STATE_ING:      // 2 - 登录中
                break;
            case AuthStateListener.AUTH_STATE_SUCCESS:  // 3 - 已认证
                break;
        }
    }
});

// 移除监听（在不需要时调用）
ListenerManager.getInstance().removeAuthStateChangeListener(listener);
```

### 3.3 退出登录

```java
// 推荐方式：通过 CoreManager 退出
// 先移除所有监听器
ListenerManager.getInstance().removeAuthStateChangeListener(authListener);
ListenerManager.getInstance().removeChatMessageListener(msgListener);
// 退出 XMPP 连接
coreManager.logout();
// 解绑服务
coreManager.destroy();

// 如需彻底停止后台服务
context.stopService(new Intent(context, CoreService.class));
```

### 3.4 自动重连

`CoreManager` 内置了自动重连机制：

```java
// 静默重连：断开后重新登录
coreManager.autoReconnect(activity);

// 带进度提示的重连
coreManager.autoReconnectShowProgress(activity);
```

---

## 4. 消息收发

### 4.1 发送消息

```java
// 通过 CoreManager 发送（推荐）
CoreManager coreManager = CoreManager.getInstance(context);

// 构造 ChatMessage
ChatMessage message = new ChatMessage();
message.setType(XmppMessage.TYPE_TEXT);      // 文本消息
message.setContent("Hello, World!");
message.setFromUserId(currentUserId);
message.setFromUserName(currentNickName);
message.setToUserId(targetUserId);
message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
// packetId 可不设置，CoreManager 会自动生成
// message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));

// 发送单聊消息
coreManager.sendChatMessage(targetUserId, message);
```

> **提示：** `CoreManager` 内部会自动为消息生成 `packetId`（如未手动设置），简化消息构造流程。

#### 支持的常用消息类型

| 类型常量 | 值 | 说明 |
|---|---|---|
| `XmppMessage.TYPE_TEXT` | 1 | 文本消息 |
| `XmppMessage.TYPE_IMAGE` | 2 | 图片消息 |
| `XmppMessage.TYPE_VOICE` | 3 | 语音消息 |
| `XmppMessage.TYPE_LOCATION` | 4 | 位置消息 |
| `XmppMessage.TYPE_VIDEO` | 6 | 视频消息 |
| `XmppMessage.TYPE_CARD` | 8 | 名片 |
| `XmppMessage.TYPE_FILE` | 9 | 文件消息 |
| `XmppMessage.TYPE_READ` | 26 | 已读回执 |
| `XmppMessage.TYPE_BACK` | 202 | 撤回消息 |
| `XmppMessage.TYPE_RED` | 28 | 红包消息 |
| `XmppMessage.TYPE_TRANSFER` | 29 | 转账消息 |

### 4.2 接收消息

注册消息监听器：

```java
ListenerManager.getInstance().addChatMessageListener(new ChatMessageListener() {
    @Override
    public void onMessageSendStateChange(int messageState, String msgId) {
        // 消息发送状态回调
        // messageState: 0=发送中, 1=发送成功, 2=发送失败
    }

    @Override
    public boolean onNewMessage(String fromUserId, ChatMessage message, boolean isGroupMsg) {
        // 新消息回调
        // fromUserId: 发送方用户ID
        // message: 消息实体
        // isGroupMsg: 是否为群消息
        // return: true=已处理消息(不再发出通知)
        return false;
    }
});

// 移除监听
ListenerManager.getInstance().removeChatMessageListener(listener);
```

### 4.3 发送已读回执

```java
// 发送已读消息（通过广播）
Intent readIntent = new Intent(OtherBroadcast.Read);
readIntent.putExtra("packetId", messagePacketId);
readIntent.putExtra("isGroup", false);          // false=单聊 true=群聊
readIntent.putExtra("friendId", fromUserId);
readIntent.putExtra("fromUserName", fromUserName);
context.sendBroadcast(readIntent);
```

### 4.4 撤回消息

撤回消息通过发送 `TYPE_BACK = 202` 类型的消息实现：

```java
ChatMessage backMessage = new ChatMessage();
backMessage.setType(XmppMessage.TYPE_BACK);
backMessage.setFromUserId(currentUserId);
backMessage.setFromUserName(currentNickName);
backMessage.setToUserId(targetUserId);
backMessage.setContent(originalPacketId);       // 被撤回消息的 packetId
backMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
// packetId 由 CoreManager 自动生成

coreManager.sendChatMessage(targetUserId, backMessage);
```

---

## 5. 群聊

### 5.1 创建群聊

```java
// 创建群聊房间
String roomId = coreManager.createMucRoom(roomName);

// 或通过指定ID创建
boolean success = coreManager.createMucRoomId(roomId);
```

### 5.2 加入/退出群聊

```java
// 加入群聊
coreManager.joinMucChat(roomId, lastSeconds);

// 退出群聊
coreManager.exitMucChat(roomId);
```

### 5.3 发送群消息

```java
ChatMessage message = new ChatMessage();
message.setType(XmppMessage.TYPE_TEXT);
message.setContent("群聊消息");
message.setFromUserId(currentUserId);
message.setFromUserName(currentNickName);
message.setToUserId(roomId);
message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
// packetId 由 CoreManager 自动生成

coreManager.sendMucChatMessage(roomId, message);
```

### 5.4 群聊监听

```java
ListenerManager.getInstance().addMucListener(new MucListener() {
    @Override
    public void onDeleteMucRoom(String toUserId) {
        // 群组被删除
    }

    @Override
    public void onMyBeDelete(String toUserId) {
        // 我被踢出群组
    }

    @Override
    public void onNickNameChange(String toUserId, String changedUserId, String changedName) {
        // 群内昵称改变
    }

    @Override
    public void onMyVoiceBanned(String toUserId, int time) {
        // 我被禁言（time=禁言时长，-1=解除禁言）
    }
});
```

---

## 6. 通讯录/好友管理

### 6.1 数据模型

#### Friend（好友）

```java
// 关键字段
friend.getUserId();         // 用户ID
friend.getNickName();       // 昵称
friend.getRemarkName();     // 备注名
friend.getDescription();    // 签名
friend.getStatus();         // 关系状态（见下方）
friend.getRoomFlag();       // 0=好友 1=群聊
friend.getUnReadNum();      // 未读消息数
friend.getContent();        // 最后一条消息内容
friend.getTimeSend();       // 最后一条消息时间
friend.getTopTime();        // 置顶时间（0=未置顶）
friend.getOfflineNoPushMsg(); // 消息免打扰 0=关闭 1=开启
```

**Friend 关系状态（status）：**

| 状态值 | 说明 |
|---|---|
| `Friend.STATUS_BLACKLIST` (-1) | 黑名单 |
| `Friend.STATUS_UNKNOW` (0) | 陌生人 |
| `Friend.STATUS_ATTENTION` (1) | 关注 |
| `Friend.STATUS_FRIEND` (2) | 好友 |
| `Friend.STATUS_SYSTEM` (8) | 系统号 |

#### NewFriendMessage（新朋友消息）

```java
// 关键字段
newFriendMessage.getType();     // 消息类型
newFriendMessage.getUserId();   // 对方用户ID
newFriendMessage.getNickName(); // 对方昵称
newFriendMessage.getContent();  // 招呼内容
newFriendMessage.getState();    // 状态
```

**NewFriendMessage 类型（type）：**

| 类型常量 | 值 | 说明 |
|---|---|---|
| `XmppMessage.TYPE_SAYHELLO` | 500 | 打招呼/添加好友 |
| `XmppMessage.TYPE_PASS` | 501 | 同意加好友 |
| `XmppMessage.TYPE_FEEDBACK` | 502 | 回话 |
| `XmppMessage.TYPE_FRIEND` | 508 | 直接成为好友 |
| `XmppMessage.TYPE_BLACK` | 507 | 黑名单 |
| `XmppMessage.TYPE_REFUSED` | 509 | 取消黑名单 |
| `XmppMessage.TYPE_DELALL` | 505 | 彻底删除 |

### 6.2 获取好友列表

好友数据存储在本地数据库中，通过 `FriendDao` 获取：

```java
// 获取当前用户的所有好友
List<Friend> friendList = FriendDao.getInstance().getFriends(ownerId);

// 获取好友列表（仅好友关系，status=2）
List<Friend> friends = new ArrayList<>();
for (Friend f : friendList) {
    if (f.getStatus() == Friend.STATUS_FRIEND) {
        friends.add(f);
    }
}

// 获取最近聊天的好友列表（用于消息界面）
List<Friend> recentList = FriendDao.getInstance().getNearlyFriendMsg(ownerId);
```

### 6.3 更新好友关系

通过 `FriendHelper` 工具类更新本地好友关系：

```java
// 从服务器获取用户信息后，更新本地好友关系
boolean changed = FriendHelper.updateFriendRelationship(loginUserId, user);

// 添加好友后的额外操作
FriendHelper.addFriendExtraOperation(loginUserId, friendId);

// 拉黑操作
FriendHelper.addBlacklistExtraOperation(loginUserId, friendId);

// 删除好友/取消关注
FriendHelper.removeAttentionOrFollow(ownerId, friendId);
```

### 6.4 发送好友请求

通过 `CoreManager` 发送新朋友消息：

```java
// 构造新朋友消息
NewFriendMessage newFriendMsg = NewFriendMessage.createWillSendMessage(
    currentUser,                    // 当前用户 User 对象
    XmppMessage.TYPE_SAYHELLO,     // 消息类型（打招呼）
    "你好，我是" + currentUser.getNickName(), // 招呼内容
    targetUserId,                   // 目标用户ID
    targetNickName,                 // 目标用户昵称
    targetCompanyId                 // 目标公司ID
);

// 发送（packetId 由 CoreManager 自动生成）
coreManager.sendNewFriendMessage(targetUserId, newFriendMsg);
```

### 6.5 监听好友请求

```java
ListenerManager.getInstance().addNewFriendListener(new NewFriendListener() {
    @Override
    public void onNewFriendSendStateChange(String toUserId, NewFriendMessage message, int messageState) {
        // 好友请求发送状态回调
        // messageState: 0=发送中, 1=发送成功, 2=发送失败
    }

    @Override
    public boolean onNewFriend(NewFriendMessage message) {
        // 收到新朋友消息
        // return: true=已处理（不再弹出通知）
        return false;
    }
});

// 移除监听
ListenerManager.getInstance().removeNewFriendListener(listener);
```

---

## 7. 数据库操作

IM 模块使用本地 SQLite 数据库存储数据，核心 DAO 类：

| DAO 类 | 说明 |
|---|---|
| `FriendDao` | 好友/联系人表操作 |
| `ChatMessageDao` | 聊天消息表操作 |
| `NewFriendDao` | 新朋友消息表操作 |
| `RoomMemberDao` | 群成员表操作 |
| `ContactDao` | 手机联系人表操作 |

### 常用 DAO 查询示例

```java
// 查询聊天记录
List<ChatMessage> messages = ChatMessageDao.getInstance().queryChatMessage(
    loginUserId, friendId, pageIndex, pageSize
);

// 查询未读消息数
int unreadCount = FriendDao.getInstance().getUnReadMessageTotal(loginUserId);

// 更新好友备注
FriendDao.getInstance().updateRemarkName(loginUserId, friendId, newRemark);

// 删除好友
FriendDao.getInstance().deleteFriend(loginUserId, friendId);
```

---

## 8. 群组专用 ID 常量

系统内置了一些特殊 ID，用于系统消息、新朋友等功能：

```java
Friend.ID_SYSTEM_MESSAGE = "10000";       // 系统消息
Friend.ID_NEW_FRIEND_MESSAGE = "10001";   // 新朋友
Friend.ID_BLOG_MESSAGE = "10002";         // 商务圈消息
Friend.ID_INTERVIEW_MESSAGE = "10004";    // 面试中心
Friend.ID_SYSTEM_NOTIFICATION = "10005";  // 系统通知
```

---

## 9. 完整集成示例

### 9.1 Activity 中使用（推荐模式）

```java
public class ChatActivity extends AppCompatActivity implements CoreStatusListener {

    private CoreManager coreManager;
    private String targetUserId;

    // ========== 监听器引用（便于移除） ==========
    private AuthStateListener authListener;
    private ChatMessageListener msgListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetUserId = getIntent().getStringExtra("targetUserId");

        // 1. 创建 CoreManager 并初始化
        coreManager = new CoreManager(this, this);
        coreManager.init(true, false);  // loginRequired=true
    }

    @Override
    public void onCoreReady() {
        // 2. 服务就绪后，注册监听
        registerListeners();
    }

    private void registerListeners() {
        // 连接状态监听
        authListener = new AuthStateListener() {
            @Override
            public void onAuthStateChange(int authState) {
                if (authState == AuthStateListener.AUTH_STATE_SUCCESS) {
                    Log.d("Chat", "XMPP 认证成功");
                }
            }
        };
        ListenerManager.getInstance().addAuthStateChangeListener(authListener);

        // 消息监听
        msgListener = new ChatMessageListener() {
            @Override
            public void onMessageSendStateChange(int messageState, String msgId) {
                // 消息发送状态：0=发送中, 1=成功, 2=失败
            }

            @Override
            public boolean onNewMessage(String fromUserId, ChatMessage message, boolean isGroupMsg) {
                // 收到新消息
                return false;
            }
        };
        ListenerManager.getInstance().addChatMessageListener(msgListener);
    }

    // ========== 发送文本消息 ==========
    private void sendTextMessage(String content) {
        ChatMessage msg = new ChatMessage();
        msg.setType(XmppMessage.TYPE_TEXT);
        msg.setContent(content);
        msg.setFromUserId(coreManager.getSelf().getUserId());
        msg.setFromUserName(coreManager.getSelf().getNickName());
        msg.setToUserId(targetUserId);
        msg.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        // packetId 自动填充，无需手动设置

        coreManager.sendChatMessage(targetUserId, msg);
    }

    // ========== 发送群消息 ==========
    private void sendGroupMessage(String roomId, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setType(XmppMessage.TYPE_TEXT);
        msg.setContent(content);
        msg.setFromUserId(coreManager.getSelf().getUserId());
        msg.setFromUserName(coreManager.getSelf().getNickName());
        msg.setToUserId(roomId);
        msg.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

        coreManager.sendMucChatMessage(roomId, msg);
    }

    // ========== 发送好友请求 ==========
    private void sendFriendRequest(User currentUser, String friendId, String friendName, String companyId) {
        NewFriendMessage nfm = NewFriendMessage.createWillSendMessage(
            currentUser, XmppMessage.TYPE_SAYHELLO, "你好",
            friendId, friendName, companyId
        );
        coreManager.sendNewFriendMessage(friendId, nfm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 3. 退出时清理：移除监听 -> 解绑服务
        if (authListener != null) {
            ListenerManager.getInstance().removeAuthStateChangeListener(authListener);
        }
        if (msgListener != null) {
            ListenerManager.getInstance().removeChatMessageListener(msgListener);
        }
        if (coreManager != null) {
            coreManager.destroy();
        }
    }
}
```

### 9.2 简单调用模式

适用于已确认 IM 服务已启动的场景（如工具类、非 UI 组件）：

```java
// 直接获取实例
CoreManager coreManager = CoreManager.getInstance(context);

if (coreManager.isServiceReady()) {
    // 发送消息
    ChatMessage msg = new ChatMessage();
    msg.setType(XmppMessage.TYPE_TEXT);
    msg.setContent("快速消息");
    msg.setFromUserId(coreManager.getSelf().getUserId());
    msg.setFromUserName(coreManager.getSelf().getNickName());
    msg.setToUserId(targetUserId);
    msg.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

    coreManager.sendChatMessage(targetUserId, msg);
}

// 检查登录状态
boolean loggedIn = coreManager.isLogin();

// 获取当前用户信息
User self = coreManager.getSelf();
String userId = self.getUserId();
String nickName = self.getNickName();

// 获取网络配置
AppConfig config = coreManager.getConfig();
```

---

## 10. 注意事项

1. **统一使用 `CoreManager`**：所有 IM 操作通过 `CoreManager` 完成，无需直接操作 `CoreService`。`CoreManager` 内部自动管理服务的启动、绑定和消息路由。
2. **必须在 REST API 登录成功后再初始化 `CoreManager`**，`init(true, false)` 会从本地读取用户登录数据。
3. **CoreService 是前台服务**，Android 8.0+ 需使用 `ContextCompat.startForegroundService()` 启动（`CoreManager.init()` 内部已处理）。
4. **packetId 自动生成**：`CoreManager` 内部会自动为未设置 `packetId` 的消息生成唯一标识，调用方可省略手动设置。
5. **消息加密**：IM SDK 支持 3DES/AES/DH+AES 多种加密方式，`Friend.getEncryptType()` 标识加密类型（0=明文, 1=3DES, 2=AES, 3=DH+AES）。
6. **消息过期**：`ChatMessage.getDeleteTime()` 标识消息的自动销毁时间，可配合 `chatRecordTimeOut` 字段实现消息限时销毁。
7. **多点登录**：通过 `TYPE_SEND_ONLINE_STATUS (200)` 协议实现多设备在线状态同步。
8. **本地消息持久化**：所有消息和好友关系均存储在本地 SQLite 数据库中，SDK 内部自动处理。
9. **生命周期管理**：Activity/Fragment 中使用时，务必在 `onDestroy()` 中调用 `coreManager.destroy()` 解绑服务，避免内存泄漏。
10. **自动重连**：`CoreManager` 提供 `autoReconnect()` 和 `autoReconnectShowProgress()` 方法处理断线重连场景。
