---
name: hyperlyric-architecture
description: HyperLyric 核心架构、文件功能、调用关系和 Root 原理说明库，用于快速判断文件作用而无需细读代码。
---

# HyperLyric Architecture & Component Skill

本技能文档详细记录了 HyperLyric 项目的核心架构、代码模块分工、以及各自在运行时（普通前台、Root/Xposed）的职责。当你需要修改系统机制或UI时，请优先参考此文档。

## 📁 目录及职责划分

### 1. 核心 Root / Xposed 模块 (`com.lidesheng.hyperlyric.root`)
该目录下的文件负责深度干预系统（提权写入、修改系统级UI），只在 Root 或被 Xposed 框架加载时生效：

- **`UnlockIslandWhitelist.kt`**
  - **功能**：Xposed 模块的实际执行者。负责 Hook MIUI SystemUI 的资源，替换媒体白名单数组（`config_dynamic_island_miniwindow_media_whitelist`），强制小米灵动岛支持第三方音乐软件。只在 HyperOS 3.0.300+ 生效。
- **`MainHook.kt`**
  - **功能**：Xposed 主入口。监听 `com.android.systemui`，等待 `IslandTemplateFactory` 加载后 Hook 其 `createBigIslandTemplateView`。动态创建并注入跑马灯 TextView（利用 Choreographer 实现 120Hz 丝滑滚动），负责直接操作修改灵动岛 UI 布局的参数和动画表现。
- **`ConfigSyncHelper.kt`**
  - **功能**：Root 提权写配置助手。读取应用的 SharedPreferences 然后拼装为字符串，利用 `ShellUtils` 在系统目录 `/data/system/hyperlyric.conf` 下强制生成配置文件，供 `MainHook` 中的 Xposed 模块越权读取。
- **`ShellUtils.kt`**
  - **功能**：纯粹的底端命令行操作类。主要作用是调用 `su -c ...` 执行 Root 单行静默命令（例如 `pkill -9 com.android.systemui` 来重启系统界面）。

### 2. 标准 Android 后台与媒体服务 (`com.lidesheng.hyperlyric`)
处理媒体控制器、通知截获、应用常驻保活，无需 Root 即可生效。

- **`LiveLyricService.kt`**
  - **功能**：通知读取服务（`NotificationListenerService`）。监听正在播放的系统 `MediaController`，抓取歌曲名、歌手、进度、专辑封面。内部分别为“灵动岛”和“系统通知”两套显示逻辑进行像素级分词（`islandTitle` vs `notificationTitle`），组合“歌曲信息”（`songInfo` = 歌手 - 歌名），并分发数据。
- **`ForegroundLyricService.kt`**
  - **功能**：前台保活与状态接力计算。它高速接盘 `LiveLyricService` 抓取的最新状态并自行模拟计算当前播放毫秒级进度，防抖动，通过前台服务常驻防止系统杀进程。
- **`NotificationManagerHelper.kt`**
  - **功能**：通知与灵动岛构建工厂。核心黑科技所在——通过特定的 JSON 字符串构建 `NotificationExtras` 骗过系统。将 `islandTitle` 映射到通知的小图标/关键文本位，将 `notificationTitle` 映射到展开态的大通知文本位，并将 `songInfo` 映射到副标题/进度条标题位。
- **`DynamicLyricData.kt`**
  - **功能**：全局缓存与状态同步总线。利用 Kotlin `MutableStateFlow` 存放 `LyricState`。包含 `islandTitleLeft/Right`（灵动岛左右分割）、`notificationTitleLeft/Right`（通知左右分割）、`songLyric`（原始歌词）以及 `songInfo`（歌手 - 歌名）等响应式数据。

### 3. UI 界面与静态辅助 (`com.lidesheng.hyperlyric.ui` 等)
- **`MainActivity.kt` & `SettingsActivity.kt` & `WhitelistActivity.kt` & `PoetryActivity.kt`**
  - **功能**：使用 `Jetpack Compose` 和 `MIUIX` (第三方高仿小米界面库) 编写的用户前台界面。提供动态的磨砂玻璃观感 (Haze) 与用户互动，所有的设置结果最终都会交由 `ConfigSyncHelper` 刷新到系统配置中。
- **`AnimUtils.kt`**
  - **功能**：歌词 UI 的 2D 基础过渡动画类（平移、缩放、淡入淡出），由 `MainHook` 在切换歌词时调用。
- **`Quotes.kt`**
  - **功能**：存储经典诗词语录静态库。
- **`Constants.kt`**
  - **功能**：存放 `SharedPreferences` 的 Key 及相关的常量默认参数。

## 💡 开发修改指北

- **如果要修改灵动岛的 UI 样式、跑马灯或者字体：**
  请前往 `MainHook.kt` (Xposed) 修改 `applyLyricContent` 与 `createBaseTextView`。
- **如果要调整白名单破解逻辑：**
  检查 `UnlockIslandWhitelist.kt` 的 Hook 点或版本要求。
- **如果发现歌词抓取/封面颜色不对：**
  调试 `LiveLyricService.kt` 中的 `MediaController` 逻辑和图片生成代码。
- **如果状态栏原生通知岛更新太慢或闪烁：**
  调试 `ForegroundLyricService.kt` 中的高频防抖或 `NotificationManagerHelper.kt` 中的 JSON 生成。
- **UI 更新，添加设置项：**
  在 `Constants.kt` 加 Key，在 `SettingsActivity.kt` 中添加组件，并在 `ConfigSyncHelper.kt` 拼接，最后去 `MainHook.kt` 增加对应的解析 `getSmartConfig()`。
