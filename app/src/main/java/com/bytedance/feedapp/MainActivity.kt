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
import com.bytedance.feedapp.constants.Strings
import com.bytedance.feedapp.ui.components.FeedList
import com.bytedance.feedapp.ui.components.FeedTabs
import com.bytedance.feedapp.ui.components.SearchBar
import com.bytedance.feedapp.ui.theme.FeedAppTheme
import com.bytedance.feedapp.viewmodel.FeedViewModel

/**
 * MainActivity 是应用的主入口 Activity。
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 setContent 将 Compose 内容设置到 Activity。
        setContent {
            // FeedAppTheme 应用自定义的 Material Design 主题。
            FeedAppTheme {
                // Surface 是一个为其子项提供背景颜色和海拔效果的容器。
                Surface(color = MaterialTheme.colorScheme.background) {
                    // FeedApp 是应用的主Composable函数。
                    FeedApp()
                }
            }
        }
    }
}

/**
 * FeedApp 是应用的主界面，负责组合搜索、标签页和信息流列表等UI组件。
 * 这个 Composable 函数遵循“状态向下流动，事件向上传递”的原则。
 *
 * @param feedViewModel ViewModel实例，提供UI所需的状态和事件处理函数。
 */
@Composable
fun FeedApp(feedViewModel: FeedViewModel = viewModel()) {
    // 从 ViewModel 中获取UI所需的状态。
    val feedItems by feedViewModel.feedItems
    val isRefreshing by feedViewModel.isRefreshing
    val selectedTabIndex by feedViewModel.selectedTabIndex
    val showSuccessMessage by feedViewModel.showSuccessMessage
    val isLoadingMore by feedViewModel.isLoadingMore
    val hasMoreData by feedViewModel.hasMoreData

    // searchText 是一个纯UI状态，保留在Composable中是合理的。
    var searchText by remember { mutableStateOf(Strings.SEARCH_TEXT_PLACEHOLDER) }

    // Box 作为根容器，允许内容层叠，例如在列表上显示提示信息。
    Box(modifier = Modifier.fillMaxSize()) {
        // Column 将子项垂直排列。
        Column {
            // 搜索栏组件。
            SearchBar(searchText = searchText, onSearchTextChange = { searchText = it })
            // 标签页组件，点击事件向上传递给ViewModel处理。
            FeedTabs(selectedTabIndex = selectedTabIndex, onTabClick = { index -> feedViewModel.onTabSelected(index) })
            // 信息流列表组件，下拉刷新和加载更多事件向上传递给ViewModel处理。
            FeedList(
                feedItems = feedItems,
                isRefreshing = isRefreshing,
                onRefresh = { feedViewModel.refreshFeedItems() },
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onLoadMore = { feedViewModel.loadMoreFeedItems() }
            )
        }

        // 根据ViewModel的状态，决定是否显示“刷新成功”的提示。
        if (showSuccessMessage) {
            Text(
                text = Strings.REFRESH_INFO,
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
    }
}

/**
 * 为 FeedApp Composable 提供在 Android Studio 中预览的功能。
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
