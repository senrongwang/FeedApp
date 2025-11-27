package com.bytedance.feedapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
 * @param exposureCallback 曝光事件回调
 * @param isSingleColumn 是否为单列布局
 * @param playingCardId 当前正在播放的视频卡片的ID，可为空。
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
    onDeleteItem: (FeedItem) -> Unit,
    exposureCallback: ExposureCallback,
    isSingleColumn: Boolean,
    playingCardId: Any? = null
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

    val gridState = rememberLazyStaggeredGridState()

    // 当用户滚动到列表末尾时，触发加载更多
    LaunchedEffect(gridState, isLoadingMore, hasMoreData) {
        snapshotFlow { gridState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount

                // 当倒数第二个项目可见，并且没有在加载，并且还有更多数据时，触发加载更多
                if (totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 2 && !isLoadingMore && hasMoreData) {
                    onLoadMore()
                }
            }
    }

    // 根据布局模式筛选数据
    val filteredItems = if (isSingleColumn) {
        feedItems.filter { it.layout == StringsConstants.FEEDITEM_SINGLE_COLUMN }
    } else {
        feedItems.filter { it.layout != StringsConstants.FEEDITEM_SINGLE_COLUMN}
    }

    // `Box` 容器应用 `nestedScroll` 连接，以将滚动事件传递给下拉刷新状态。
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        // 使用 `LazyVerticalStaggeredGrid` 来实现瀑布流布局
        LazyVerticalStaggeredGrid(
            columns = if (isSingleColumn) StaggeredGridCells.Fixed(1) else StaggeredGridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalItemSpacing = 1.dp
        ) {
            items(filteredItems, key = { it.id }, span = { item ->
                if (item.layout == StringsConstants.FEEDITEM_SINGLE_COLUMN) {
                    StaggeredGridItemSpan.FullLine
                } else {
                    StaggeredGridItemSpan.SingleLane
                }
            }) { item ->
                // 从 `CardRegistry` 中获取与信息流项目类型对应的 Composable 函数，并调用它来渲染卡片。
                CardRegistry.getCard(item.type)?.invoke(item, onDeleteItem, playingCardId)

                TrackCardExposure(
                    gridState = gridState,
                    cardId = item.id,
                    callback = exposureCallback
                )
            }

            // 在列表末尾显示加载中或没有更多数据的指示器
            item(span = StaggeredGridItemSpan.FullLine) {
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
