package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView // 网格图标
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerCardViews()
        setContent {
            FeedAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FeedApp()
                }
            }
        }
    }

    private fun registerCardViews() {
        CardRegistry.registerCard("text") { item, onLongPress, _ ->
            if (item is TextFeedItem) {
                TextCard(item = item, onLongPress = { onLongPress(it) })
            }
        }
        CardRegistry.registerCard("image") { item, onLongPress, _ ->
            if (item is ImageFeedItem) {
                ImageCard(item = item, onLongPress = { onLongPress(it) })
            }
        }
        // 更新 "video" 卡片的注册，以接受 playingCardId
        // 并将 isPlaying 参数传递给 VideoCard
        CardRegistry.registerCard("video") { item, onLongPress, playingCardId ->
            if (item is VideoFeedItem) {
                VideoCard(
                    item = item,
                    onLongPress = { onLongPress(it) },
                    isPlaying = item.id == playingCardId
                )
            }
        }
        CardRegistry.registerCard("product") { item, onLongPress, _ ->
            if (item is ProductFeedItem) {
                ProductCard(item = item, onLongPress = { onLongPress(it) })
            }
        }
    }
}

@Composable
fun FeedApp(feedViewModel: FeedViewModel = viewModel()) {
    val feedItems by feedViewModel.feedItems
    val isRefreshing by feedViewModel.isRefreshing
    val selectedTabIndex by feedViewModel.selectedTabIndex
    val showSuccessMessage by feedViewModel.showSuccessMessage
    val errorMessage by feedViewModel.showErrorMessage
    val isLoadingMore by feedViewModel.isLoadingMore
    val hasMoreData by feedViewModel.hasMoreData
    val showDeleteConfirmationDialog by feedViewModel.showDeleteConfirmationDialog

    var searchText by remember { mutableStateOf(StringsConstants.SEARCH_TEXT_PLACEHOLDER) }
    var isSingleColumn by remember { mutableStateOf(true) }

    val exposureCallback = remember { TestExposureCallback() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // 搜索框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(searchText = searchText, onSearchTextChange = { searchText = it })
                }
            }
            // 不同状态栏
            FeedTabs(selectedTabIndex = selectedTabIndex, onTabClick = { index -> feedViewModel.onTabSelected(index) })
            // 切换单列双列布局按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { isSingleColumn = !isSingleColumn }) {
                    Icon(imageVector = if (isSingleColumn) Icons.AutoMirrored.Filled.List else Icons.Default.GridView, contentDescription = "Switch Layout")
                }
            }
            // 卡片列表
            FeedList(
                feedItems = feedItems,
                isRefreshing = isRefreshing,
                onRefresh = { feedViewModel.refreshFeedItems() },
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onLoadMore = { feedViewModel.loadMoreFeedItems() },
                onDeleteItem = { item -> feedViewModel.onDeleteItem(item) },
                exposureCallback = exposureCallback,
                isSingleColumn = isSingleColumn,
                // 传递当前播放的视频ID
                playingCardId = exposureCallback.playingCardId
            )
        }

        if (showDeleteConfirmationDialog) {
            DeleteConfirmationDialog(
                onConfirm = { feedViewModel.confirmDeleteItem() },
                onCancel = { feedViewModel.cancelDeleteItem() }
            )
        }

        if (showSuccessMessage) {
            Text(
                text = StringsConstants.REFRESH_INFO,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        errorMessage?.let { msg ->
            var snackbarVisible by remember { mutableStateOf(true) }
            LaunchedEffect(errorMessage) {
                snackbarVisible = true
                delay(2000) // 2秒后自动消失
                snackbarVisible = false
            }
            if (snackbarVisible) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                ) {
                    Text(text = msg)
                }
            }
        }
        // 在屏幕上显示曝光测试工具
//        ExposureTestTool(testExposureCallback = exposureCallback)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
