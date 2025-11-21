package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.feedapp.constants.StringsConstants
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem
import com.bytedance.feedapp.ui.components.CardRegistry
import com.bytedance.feedapp.ui.components.ExposureTestTool
import com.bytedance.feedapp.ui.components.FeedList
import com.bytedance.feedapp.ui.components.FeedTabs
import com.bytedance.feedapp.ui.components.SearchBar
import com.bytedance.feedapp.ui.components.TestExposureCallback
import com.bytedance.feedapp.ui.components.cards.ImageCard
import com.bytedance.feedapp.ui.components.cards.ProductCard
import com.bytedance.feedapp.ui.components.cards.TextCard
import com.bytedance.feedapp.ui.components.cards.VideoCard
import com.bytedance.feedapp.ui.components.dialogs.DeleteConfirmationDialog
import com.bytedance.feedapp.ui.theme.FeedAppTheme
import com.bytedance.feedapp.viewmodel.FeedViewModel

/**
 * `MainActivity` 是应用的主入口 Activity。
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在应用启动时注册所有可用的卡片视图。
        registerCardViews()
        // 使用 `setContent` 将 Compose 内容设置到 Activity。
        setContent {
            // `FeedAppTheme` 应用自定义的 Material Design 主题。
            FeedAppTheme {
                // `Surface` 是一个为其子项提供背景颜色和高程效果的容器。
                Surface(color = MaterialTheme.colorScheme.background) {
                    // `FeedApp` 是应用的主 Composable 函数。
                    FeedApp()
                }
            }
        }
    }

    /**
     * `registerCardViews` 函数负责将所有卡片类型的 Composable 函数注册到 `CardRegistry` 中。
     * 这种方法使得在不修改核心逻辑的情况下，可以轻松扩展新的卡片类型。
     */
    private fun registerCardViews() {
        CardRegistry.registerCard("text") { item, onLongPress ->
            if (item is TextFeedItem) {
                TextCard(item = item, onLongPress = onLongPress)
            }
        }
        CardRegistry.registerCard("image") { item, onLongPress ->
            if (item is ImageFeedItem) {
                ImageCard(item = item, onLongPress = onLongPress)
            }
        }
        CardRegistry.registerCard("video") { item, onLongPress ->
            if (item is VideoFeedItem) {
                VideoCard(item = item, onLongPress = onLongPress)
            }
        }
        CardRegistry.registerCard("product") { item, onLongPress ->
            if (item is ProductFeedItem) {
                ProductCard(item = item, onLongPress = onLongPress)
            }
        }
    }
}

/**
 * `FeedApp` 是应用的主界面，负责组合搜索、标签页和信息流列表等 UI 组件。
 * 这个 Composable 函数遵循“状态向下流动，事件向上传递”的原则。
 *
 * @param feedViewModel ViewModel 实例，提供 UI 所需的状态和事件处理函数。
 */
@Composable
fun FeedApp(feedViewModel: FeedViewModel = viewModel()) {
    // 从 ViewModel 中获取 UI 所需的状态。
    val feedItems by feedViewModel.feedItems
    val isRefreshing by feedViewModel.isRefreshing
    val selectedTabIndex by feedViewModel.selectedTabIndex
    val showSuccessMessage by feedViewModel.showSuccessMessage
    val isLoadingMore by feedViewModel.isLoadingMore
    val hasMoreData by feedViewModel.hasMoreData
    val showDeleteConfirmationDialog by feedViewModel.showDeleteConfirmationDialog

    // `searchText` 是一个纯 UI 状态，保留在 Composable 中是合理的。
    var searchText by remember { mutableStateOf(StringsConstants.SEARCH_TEXT_PLACEHOLDER) }

    val exposureCallback = remember { TestExposureCallback() }

    // `Box` 作为根容器，允许内容层叠，例如在列表上显示提示信息。
    Box(modifier = Modifier.fillMaxSize()) {
        // `Column` 将子项垂直排列。
        Column {
            // 搜索栏组件。
            SearchBar(searchText = searchText, onSearchTextChange = { searchText = it })
            // 标签页组件，点击事件向上传递给 ViewModel 处理。
            FeedTabs(selectedTabIndex = selectedTabIndex, onTabClick = { index -> feedViewModel.onTabSelected(index) })
            // 信息流列表组件，下拉刷新和加载更多事件向上传递给 ViewModel 处理。
            FeedList(
                feedItems = feedItems,
                isRefreshing = isRefreshing,
                onRefresh = { feedViewModel.refreshFeedItems() },
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onLoadMore = { feedViewModel.loadMoreFeedItems() },
                onDeleteItem = { item -> feedViewModel.onDeleteItem(item) },
                exposureCallback = exposureCallback
            )
        }

        // 根据 ViewModel 的状态，决定是否显示删除确认对话框。
        if (showDeleteConfirmationDialog) {
            DeleteConfirmationDialog(
                onConfirm = { feedViewModel.confirmDeleteItem() },
                onCancel = { feedViewModel.cancelDeleteItem() }
            )
        }

        // 根据 ViewModel 的状态，决定是否显示“刷新成功”的提示。
        if (showSuccessMessage) {
            Text(
                text = StringsConstants.REFRESH_INFO,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // 居于底部中心
                    .padding(16.dp) // 外边距
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer, // 背景色
                        shape = RoundedCornerShape(8.dp) // 圆角
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp), // 内边距
                color = MaterialTheme.colorScheme.onPrimaryContainer // 文字颜色
            )
        }

        // 在屏幕上显示曝光测试工具
        ExposureTestTool(testExposureCallback = exposureCallback)
    }
}

/**
 * 为 `FeedApp` Composable 提供在 Android Studio 中预览的功能。
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}