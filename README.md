# WorkUp-SDK 全部 SDK 仓库及模块总览

> **文档版本**: v1.0  
> **最后更新**: 2026-07-23  
> **适用 SDK 版本**: omm-lib 1.0.0+

---

## 一、SDK 仓库总览

WorkUp-SDK 项目共包含 **3 个 SDK 仓库**，分布在 `C:\AndroidProjects\SDK\SDK\WorkUp-SDK` 目录下：

| 序号 | 仓库目录 | SDK 名称 | 包含模块 | 说明 |
|:---:|---------|---------|---------|------|
| 1 | `IM/` | **IMSDK** | IM（即时通讯） | 纯 IM 即时通讯 SDK |
| 2 | `IM-MPAAS-APPSTORE/` | **IM-MPAAS-APPSTORE-SDK** | IM + AppStore | IM 即时通讯 + 应用市场 组合 SDK |
| 3 | `MPAAS-APPSTORE/` | **MPAAS-APPSTORE-SDK** | AppStore（应用市场） | 纯应用市场 SDK |

每个仓库下均包含：
- **SDK 源码目录**（如 `IMSDK/`）：包含集成声明文档
- **Demo 工程目录**（如 `IMSDK-Demo/`）：可直接运行的集成示例
- **集成指南**（`xxx集成指南.md`）
- **更新日志**（`xxx更新日志.md`）

---

## 二、各 SDK 详细说明

### 2.1 IMSDK（即时通讯 SDK）

| 属性 | 值 |
|------|-----|
| **仓库路径** | `WorkUp-SDK/IM/` |
| **Demo 工程名** | `IMSDK-Demo` |
| **Application ID** | `com.oort.im` |
| **核心模块** | `omm-lib` |
| **技术栈** | XMPP 协议 + Smack 4.4.6 + SQLite |
| **当前依赖版本** | `com.oort.workup-sdk:omm-lib:1.0.4` |
| **GitHub Release** | https://github.com/OortCloudGroup/IM/releases/ |

**功能清单：**

| 功能分类 | 具体能力 |
|---------|---------|
| **单聊** | 文本、图片、语音、视频、位置、文件、名片、红包、转账消息 |
| **群聊** | 创建/加入/退出群组，群消息收发，群内昵称修改，禁言管理 |
| **好友管理** | 添加/删除好友，黑名单，关注/取关，备注名，好友请求 |
| **消息安全** | 已读回执，消息撤回，3DES/AES/DH+AES 加密，消息过期自动销毁 |
| **多点登录** | 多设备在线状态同步 |
| **离线消息** | 离线消息拉取与消息漫游 |
| **数据持久化** | 基于 SQLite 的本地存储（FriendDao、ChatMessageDao、NewFriendDao、RoomMemberDao、ContactDao） |
| **核心入口** | `CoreManager` 门面类，封装服务绑定、消息收发、群聊等全部操作 |
| **后台服务** | `CoreService` 前台服务，保障消息推送稳定性 |

---

### 2.2 IM-MPAAS-APPSTORE-SDK（IM + 应用市场组合 SDK）

| 属性 | 值 |
|------|-----|
| **仓库路径** | `WorkUp-SDK/IM-MPAAS-APPSTORE/` |
| **Demo 工程名** | `IM-MPAAS-APPSTORE-SDK-Demo` |
| **Application ID** | `com.oort.imappstore` |
| **核心模块** | `omm-lib` |
| **技术栈** | IM: Smack 4.4.6 + SQLite；AppStore: RxJava2 + Retrofit2 + LitePal + Glide |
| **当前依赖版本** | `com.oort.workup-sdk:omm-lib:1.0.4` |
| **GitHub Release** | https://github.com/OortCloudGroup/IM-MPAAS-APPSTORE/releases/tag/v1.0.0 |

**功能清单：**

包含 IMSDK 的全部 IM 功能 + 以下 AppStore 功能：

| 功能分类 | 具体能力 |
|---------|---------|
| **应用展示** | 推荐页、分类页、搜索、当月最新应用 |
| **应用详情** | 详情页展示，支持 APK / H5 / Web 三种终端类型 |
| **下载管理** | 内置 DownloadManager，支持断点续传与进度回调 |
| **安装管理** | 自动检测本地安装状态，引导用户完成安装 |
| **版本更新** | 版本检测与增量更新提示 |
| **评论评分** | 评论列表、评分、发表评论与回复 |
| **模块管理** | 应用模块列表初始化与动态更新 |
| **应用管理** | 已安装应用查看与管理 |

---

### 2.3 MPAAS-APPSTORE-SDK（应用市场 SDK）

| 属性 | 值 |
|------|-----|
| **仓库路径** | `WorkUp-SDK/MPAAS-APPSTORE/` |
| **Demo 工程名** | `MPAAS-APPSTORE-SDK-Demo` |
| **Application ID** | `com.oort.apppstore` |
| **核心模块** | `omm-lib` |
| **技术栈** | RxJava2 + Retrofit2 + LitePal + Glide |
| **当前依赖版本** | `com.oort.workup-sdk:omm-lib:1.0.4` |
| **GitHub Release** | https://github.com/OortCloudGroup/MPAAS-APPSTORE/releases |

**功能清单：**

| 功能分类 | 具体能力 |
|---------|---------|
| **应用展示** | 推荐页、分类页、搜索、当月最新应用 |
| **应用详情** | 详情页展示，支持 APK / H5 / Web 三种终端类型 |
| **下载管理** | 内置 DownloadManager，支持断点续传与进度回调 |
| **安装管理** | 自动检测本地安装状态，引导用户完成安装 |
| **版本更新** | 版本检测与增量更新提示 |
| **评论评分** | 评论列表、评分、发表评论与回复 |
| **模块管理** | 应用模块列表初始化与动态更新 |
| **应用管理** | 已安装应用查看与管理 |

---

## 三、Maven 仓库地址

所有 SDK 均通过以下 Maven 仓库发布：

| 仓库类型 | 地址 | 说明 |
|---------|------|------|
| **Nexus（企业内网）** | `http://192.168.88.218:8403/repository/workup-sdk-releases/` | 主仓库，HTTP 协议 |
| **GitLab Maven（备用）** | `http://192.168.88.125/zhangzhijun/workup-sdk-maven/raw/master` | 备用仓库，需 Token 认证 |
| **JitPack（公开）** | `https://jitpack.io` | GitHub 公开仓库，无需认证 |

**Maven 坐标：**

| 来源 | GroupId | ArtifactId | 版本 |
|------|---------|-----------|------|
| Nexus / GitLab | `com.oort.workup-sdk` | `omm-lib` | `1.0.4` |
| JitPack | `com.github.Bean-V.WorkUp-SDK` | `omm-lib` | `1.0.0` |

---

## 四、SDK 功能对比矩阵

| 功能 | IMSDK | IM-MPAAS-APPSTORE-SDK | MPAAS-APPSTORE-SDK |
|------|:-----:|:--------------------:|:------------------:|
| IM 即时通讯 | 支持 | 支持 | 不支持 |
| 单聊消息 | 支持 | 支持 | 不支持 |
| 群聊消息 | 支持 | 支持 | 不支持 |
| 好友管理 | 支持 | 支持 | 不支持 |
| 消息加密 | 支持 | 支持 | 不支持 |
| 已读回执/撤回 | 支持 | 支持 | 不支持 |
| 多点登录 | 支持 | 支持 | 不支持 |
| 应用展示 | 不支持 | 支持 | 支持 |
| 应用下载/安装 | 不支持 | 支持 | 支持 |
| 版本更新检测 | 不支持 | 支持 | 支持 |
| 评论评分 | 不支持 | 支持 | 支持 |
| 应用管理 | 不支持 | 支持 | 支持 |

---

## 五、公共构建配置

所有 Demo 工程使用统一的构建配置：

| 配置项 | 值 |
|-------|-----|
| **最低 SDK** | 26（Android 8.0） |
| **目标 SDK** | 36（Android 16） |
| **编译 SDK** | 37 |
| **Java 版本** | 17 |
| **Kotlin 版本** | 2.3.20（编译器）/ 2.4.10（Gradle 插件） |
| **Gradle Plugin** | 9.2.1 |
| **React Native** | 0.77.2 |

---

## 六、前置依赖模块

使用远程依赖时，以下模块会通过 POM 自动引入。使用本地 AAR 时需确保宿主已引入：

| 依赖模块 | 说明 |
|---------|------|
| `:core` | 基础框架（用户信息、网络常量等） |
| `:ooortCloudDisk` | 云盘模块（AppStore 内打开云盘时使用） |

---

## 七、目录结构总览

```
WorkUp-SDK/
├── IM/                                    # IMSDK 仓库
│   ├── IMSDK/                             # SDK 源码（含集成声明）
│   │   └── SDK查看集成声明 .md
│   ├── IMSDK-Demo/                        # Demo 工程
│   │   ├── app/                           # App 模块
│   │   ├── build.gradle                   # 根构建脚本
│   │   └── settings.gradle                # 设置文件
│   ├── IMSDK集成指南.md                    # 集成文档
│   └── IMSDK更新日志.md                    # 变更记录
│
├── IM-MPAAS-APPSTORE/                     # IM+AppStore 组合 SDK 仓库
│   ├── IM-MPAAS-APPSTORE-SDK/             # SDK 源码（含集成声明）
│   │   └── SDK查看集成声明 .md
│   ├── IM-MPAAS-APPSTORE-SDK-Demo/        # Demo 工程
│   │   ├── app/                           # App 模块
│   │   ├── build.gradle                   # 根构建脚本
│   │   └── settings.gradle                # 设置文件
│   ├── IM-MPAAS-APPSTORE-SDK集成指南.md    # 集成文档
│   └── IM-MPAAS-APPSTORE-SDK更新日志.md    # 变更记录
│
└── MPAAS-APPSTORE/                        # AppStore 仓库
    ├── MPAAS-APPSTORE-SDK/                # SDK 源码（含集成声明）
    │   └── SDK查看集成声明 .md
    ├── MPAAS-APPSTORE-SDK-Demo/           # Demo 工程
    │   ├── app/                           # App 模块
    │   ├── build.gradle                   # 根构建脚本
    │   └── settings.gradle                # 设置文件
    ├── MPAAS-APPSTORE-SDK集成指南.md       # 集成文档
    └── MPAAS-APPSTORE-SDK更新日志.md       # 变更记录
```

---

## 八、2.0.0-rc.20261117.3（新增，2026-11-20）

对应官网下载页 6 张卡片的**新版模块化 SDK**（RC 集成候选，2026-11-17 构建，非正式发布版）。旧的 `IM/`、`MPAAS-APPSTORE/`、`IM-MPAAS-APPSTORE/` 三套 v1.x 目录保留不动。

```
2.0.0-rc.20261117.3/
├── 01-IM/                        # IM
├── 02-APPSTORE/                  # 应用市场
├── 03-IM-APPSTORE/               # IM + 应用市场
├── 04-AV/                        # 音视频
├── 05-IM-APPSTORE-AV/            # IM + 应用市场 + 音视频
├── 06-HOME-IM-APPSTORE-AV/       # 首页 + IM + 应用市场 + 音视频
├── maven-repo/                   # 完整 file-based Maven 仓库（全部模块 AAR + POM）
└── apks/                         # 6 个单 SDK demo APK（开发签名，仅验收用）
```

- 每个卡片目录含 `README.md`（组成/坐标/依赖写法）与 `docs/`（集成指南、更新日志）。
- 本版为**模块化发布**：组合卡片按 README 列出的坐标引入即可，底座依赖由 `maven-repo/` 的 POM 自动解析。
- 音视频模块需宿主额外引入 `io.github.webrtc-sdk:android:125.6422.07`。
- 组合宿主 APK（OA2443）因超过 GitHub 单文件 100MB 限制未随仓库分发。
- 正式发布阻塞项（16KB 兼容、RTMP 串流、离线推送等）详见交付文档，本目录内容仅用于集成与验收。

