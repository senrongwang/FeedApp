package com.bytedance.feedapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.feedapp.model.FeedItem
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.LoadingFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem

/**
 * FeedList 是一个 Composable 函数，用于显示信息流项目列表，并支持下拉刷新功能。
 *
 * @param feedItems 要显示的信息流项目列表。
 * @param isRefreshing 指示当前是否正在刷新的布尔值。
 * @param onRefresh 当用户执行下拉刷新操作时调用的回调函数。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedList(
    feedItems: List<FeedItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    // 创建并记住一个下拉刷新的状态控制器。
    val state = rememberPullToRefreshState()
    // 如果状态控制器报告正在刷新（由用户下拉触发），则调用 onRefresh 回调。
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    // 当外部的 isRefreshing 状态改变时，同步到下拉刷新状态控制器。
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            // 如果刷新结束，则调用 endRefresh 来隐藏刷新指示器。
            state.endRefresh()
        }
    }

    // Box 容器应用 nestedScroll 连接，以将滚动事件传递给下拉刷新状态。
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        // LazyColumn 用于高效地显示大量或无限的列表项。
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // items 函数遍历 feedItems 列表，为每个项目创建一个 Composable。
            items(feedItems) { item ->
                // 根据信息流项目的类型，选择并渲染对应的卡片 Composable。
                when (item) {
                    is TextFeedItem -> TextCard(item)
                    is ImageFeedItem -> ImageCard(item)
                    is VideoFeedItem -> VideoCard(item)
                    is ProductFeedItem -> ProductCard(item)
                    is LoadingFeedItem -> LoadingCard()
                }
            }
        }
        // 仅在用户正在下拉或刷新进行中时，才显示下拉刷新容器。
        // 这可以防止在静止状态下显示灰色的背景圆圈。
        if (state.isRefreshing || state.progress > 0) {
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter), // 将刷新指示器对齐到顶部中心。
                state = state, // 将刷新指示器与状态控制器关联。
            )
        }
    }
}

/**
 * 显示纯文本信息流项目的卡片。
 */
@Composable
fun TextCard(item: TextFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Text(text = item.text, modifier = Modifier.padding(16.dp))
    }
}

/**
 * 显示带图片和文本的信息流项目的卡片。
 */
@Composable
fun ImageCard(item: ImageFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}

/**
 * 显示视频信息流项目的卡片（用一个播放图标作为占位符）。
 */
@Composable
fun VideoCard(item: VideoFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Play Video",
                    tint = Color.White
                )
            }
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}

/**
 * 显示产品信息流项目的卡片，包括图片、名称和价格。
 */
@Composable
fun ProductCard(item: ProductFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name, modifier = Modifier.weight(1f))
                Text(text = item.price, color = Color.Red)
            }
        }
    }
}

/**
 * 显示一个加载指示器，用于表示正在加载更多项目。
 */
@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
