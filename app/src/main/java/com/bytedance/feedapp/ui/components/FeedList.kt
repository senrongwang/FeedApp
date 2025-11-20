package com.bytedance.feedapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.constants.StringsConstants
import com.bytedance.feedapp.model.FeedItem

/**
 * `FeedList` 是一个 Composable 函数，用于显示信息流项目列表，并支持下拉刷新和无限滚动加载功能。
 *
 * @param feedItems 要显示的信息流项目列表。
 * @param isRefreshing 指示当前是否正在刷新的布尔值。
 * @param onRefresh 当用户执行下拉刷新操作时调用的回调函数。
 * @param isLoadingMore 指示当前是否正在加载更多的布尔值。
 * @param hasMoreData 指示是否还有更多数据可供加载。
 * @param onLoadMore 当列表滚动到底部时调用的回调函数。
 * @param onDeleteItem 当用户长按某个信息流项目时调用的回调函数。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedList(
    feedItems: List<FeedItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    isLoadingMore: Boolean,
    hasMoreData: Boolean,
    onLoadMore: () -> Unit,
    onDeleteItem: (FeedItem) -> Unit
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

    val listState = rememberLazyListState()

    // 当用户滚动到列表末尾时，触发加载更多
    LaunchedEffect(listState, isLoadingMore, hasMoreData) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount

                // 当倒数第二个项目可见，并且没有在加载，并且还有更多数据时，触发加载更多
                if (totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 2 && !isLoadingMore && hasMoreData) {
                    onLoadMore()
                }
            }
    }

    // `Box` 容器应用 `nestedScroll` 连接，以将滚动事件传递给下拉刷新状态。
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        // `LazyColumn` 用于高效地显示大量或无限的列表项。
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // `items` 函数遍历 `feedItems` 列表，为每个项目创建一个 Composable。
            items(feedItems) { item ->
                // 从 `CardRegistry` 中获取与信息流项目类型对应的 Composable 函数，并调用它来渲染卡片。
                CardRegistry.getCard(item.type)?.invoke(item, onDeleteItem)
            }

            // 在列表末尾显示加载中或没有更多数据的指示器
            item {
                if (isLoadingMore) {
                    LoadingMoreIndicator()
                } else if (!hasMoreData) {
                    NoMoreDataIndicator()
                }
            }
        }
        // 仅在用户正在下拉或刷新进行中时，才显示下拉刷新容器。
        if (state.isRefreshing || state.progress > 0) {
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter), // 将刷新指示器对齐到顶部中心。
                state = state, // 将刷新指示器与状态控制器关联。
            )
        }
    }
}

/**
 * 显示一个加载指示器，用于表示正在加载更多项目。
 */
@Composable
private fun LoadingMoreIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 显示 "没有更多内容了" 的提示。
 */
@Composable
private fun NoMoreDataIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = StringsConstants.NO_MORE_DATA)
    }
}