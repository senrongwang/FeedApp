基础功能
- 所有列表内容均由服务端数据下发（可创建一些模拟数据在本地进行模拟） done
- 支持下拉刷新功能  done
- 支持无限加载更多（loadMore）功能  done
- 支持删卡操作，长按卡片出删卡确认对话框，确认后删卡 done
- 具备多种卡片样式，卡片包含文字和图片，卡片样式及数据内容均由服务端数据下发（不同卡片样式可做象征性展示）。 done
- 支持排版方式混排，每张卡片均可对排版方式进行控制，可选择单列或双列排版，卡片的排版方式由服务端进行控制。 done （添加了切换单双列按钮）
- 实现卡片曝光事件（一种精准的、卡片在列表中露出时的事件回调） done（修改MainActivity.kt下的showExposureTestTool来开关测试工具）


功能点扩展
- 本地数据缓存：在网络请求失败的情况下，使用本地缓存进行展示。<br>
  网络请求失败时，读取缓存中的feed_cache_data.json进行显示。
- 实现视频的自动播放与停止播放。<br>
  使用倒计时显示来模拟播放器，首先播放完整显示并且最靠上的视频。

架构<br>
com.bytedance.feedapp<br>
├── constants<br>
│   └── AppConstants.kt           <-- 全局常量 (整合了原 String/Integer 常量)<br>
├── data<br>
│   └── MockRepo.kt               <-- 数据仓库 (负责网络模拟与本地缓存)<br>
├── model<br>
│   └── FeedItem.kt               <-- 数据实体模型 (Gson 解析对象)<br>
├── ui<br>
│   ├── activity                  <-- 页面容器<br>
│   │   ├── MainActivity.kt<br>
│   │   └── SplashActivity.kt<br>
│   ├── components                <-- UI 视图组件层<br>
│   │   ├── cards                 <-- 具体业务卡片 UI<br>
│   │   │   ├── ImageCard.kt<br>
│   │   │   ├── ProductCard.kt<br>
│   │   │   ├── TextCard.kt<br>
│   │   │   └── VideoCard.kt<br>
│   │   ├── common                <-- 通用/无业务逻辑组件<br>
│   │   │   └── SearchBar.kt<br>
│   │   ├── dialogs               <-- 弹窗组件<br>
│   │   │   └── DeleteConfirmationDialog.kt<br>
│   │   └── feed                  <-- 业务核心组件 (强耦合业务逻辑)<br>
│   │       ├── FeedList.kt<br>
│   │       └── FeedTabs.kt<br>
│   ├── debug                     <-- 调试专用工具 (可分离)<br>
│   │   └── ExposureDebugOverlay.kt<br>
│   ├── helper                    <-- 逻辑与状态辅助 (非 UI)<br>
│   │   ├── CardRegistry.kt       <-- 卡片工厂/注册表<br>
│   │   ├── FeedExposureUtils.kt  <-- 曝光计算纯逻辑<br>
│   │   └── FeedPlaybackManager.kt<-- 自动播放业务状态管理<br>
│   └── theme                     <-- Compose 主题样式<br>
│       ├── Color.kt<br>
│       ├── Theme.kt<br>
│       └── Type.kt<br>
└── viewmodel<br>
    └── FeedViewModel.kt          <-- 状态持有者 (MVVM 核心)<br>
