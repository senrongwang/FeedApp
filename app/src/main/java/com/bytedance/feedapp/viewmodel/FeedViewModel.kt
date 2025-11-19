package com.bytedance.feedapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.feedapp.constants.Integers
import com.bytedance.feedapp.constants.Strings
import com.bytedance.feedapp.data.MockRepo
import com.bytedance.feedapp.model.FeedItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * FeedViewModel 负责管理信息流（Feed）的数据和业务逻辑。
 * 它处理数据的获取、刷新，并维护UI所需的状态。
 */
class FeedViewModel : ViewModel() {
    // `feedItems` 用于存储当前显示在界面上的信息流项目列表。
    val feedItems = mutableStateOf<List<FeedItem>>(emptyList())
    // `isRefreshing` 标记当前是否正在执行下拉刷新操作。
    val isRefreshing = mutableStateOf(false)
    // `selectedTabIndex` 管理当前选中的标签页索引。
    val selectedTabIndex = mutableStateOf(0)
    // `showSuccessMessage` 控制“刷新成功”提示的显示状态。
    val showSuccessMessage = mutableStateOf(false)
    // `isLoadingMore` 标记当前是否正在执行加载更多操作。
    val isLoadingMore = mutableStateOf(false)
    // `hasMoreData` 标记是否还有更多数据可供加载。
    val hasMoreData = mutableStateOf(true)

    // `currentPage` 用于跟踪加载更多时的分页。
    private var currentPage = 1

    init {
        // 初始化时获取第一个标签页的数据。
        fetchFeedItemsForTab(Strings.TABS[selectedTabIndex.value])
    }

    /**
     * 处理标签页选择事件。
     *
     * @param index 被选中的标签页的索引。
     */
    fun onTabSelected(index: Int) {
        selectedTabIndex.value = index
        fetchFeedItemsForTab(Strings.TABS[index])
    }

    /**
     * 根据指定的标签页获取信息流数据。
     *
     * @param tab 当前选中的标签页名称。
     */
    private fun fetchFeedItemsForTab(tab: String) {
        // 重置分页状态
        currentPage = 1
        hasMoreData.value = true
        // 为了演示，我们从MockRepo获取数据。在真实应用中，这里会进行网络请求。
        feedItems.value = MockRepo.getFeedItemsForTab(tab).shuffled()
    }

    /**
     * 刷新当前标签页的信息流数据。
     */
    fun refreshFeedItems() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(Integers.REFRESH_DELAY) // 模拟网络请求延迟。

            // 刷新时重置分页并获取第一页数据
            currentPage = 1
            hasMoreData.value = true
            feedItems.value = MockRepo.getFeedItemsForTab(Strings.TABS[selectedTabIndex.value]).shuffled()

            isRefreshing.value = false
            // 刷新成功后显示提示信息。
            showSuccessMessage.value = true
            delay(Integers.SUCCESS_MESSAGE_DELAY) // 提示显示2秒。
            showSuccessMessage.value = false
        }
    }

    /**
     * 加载更多信息流数据。
     */
    fun loadMoreFeedItems() {
        // 如果正在加载或没有更多数据，则直接返回
        if (isLoadingMore.value || !hasMoreData.value) return

        viewModelScope.launch {
            isLoadingMore.value = true
            currentPage++
            delay(Integers.REFRESH_DELAY) // 模拟网络延迟

            // 假设页码大于2时没有更多数据
            val newItems = if (currentPage > 2) {
                emptyList()
            } else {
                MockRepo.getFeedItemsForTab(Strings.TABS[selectedTabIndex.value]).shuffled()
            }

            if (newItems.isNotEmpty()) {
                feedItems.value = feedItems.value + newItems
            } else {
                hasMoreData.value = false
            }

            isLoadingMore.value = false
        }
    }
}
