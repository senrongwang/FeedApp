package com.bytedance.feedapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.feedapp.constants.IntegersConstants
import com.bytedance.feedapp.constants.StringsConstants
import com.bytedance.feedapp.data.MockRepo
import com.bytedance.feedapp.model.FeedItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * `FeedViewModel` 负责管理信息流（Feed）的数据和业务逻辑。
 * 它处理数据的获取、刷新、删除，并维护 UI 所需的状态。
 */
class FeedViewModel(application: Application) : AndroidViewModel(application) {
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
    // `showDeleteConfirmationDialog` 控制删除确认对话框的显示状态。
    val showDeleteConfirmationDialog = mutableStateOf(false)
    // `itemToDelete` 用于暂存待删除的信息流项目。
    val itemToDelete = mutableStateOf<FeedItem?>(null)

    // `currentPage` 用于跟踪加载更多时的分页。
    private var currentPage = 1
    private val pageSize = 5 // 每页加载的数量

    init {
        // 初始化时加载并解析JSON数据，然后获取第一个标签页的数据。
        MockRepo.loadAndParseFeedData(application)
        fetchInitialFeedItems(StringsConstants.TABS[selectedTabIndex.value])
    }

    /**
     * 处理标签页选择事件。
     *
     * @param index 被选中的标签页的索引。
     */
    fun onTabSelected(index: Int) {
        selectedTabIndex.value = index
        fetchInitialFeedItems(StringsConstants.TABS[index])
    }

    /**
     * 根据指定的标签页获取初始信息流数据。
     *
     * @param tab 当前选中的标签页名称。
     */
    private fun fetchInitialFeedItems(tab: String) {
        // 重置分页状态
        currentPage = 1
        hasMoreData.value = true
        feedItems.value = MockRepo.getFeedItemsForTab(tab, currentPage, pageSize)
    }

    /**
     * 刷新当前标签页的信息流数据。
     */
    fun refreshFeedItems() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(IntegersConstants.REFRESH_DELAY) // 模拟网络请求延迟。

            // 刷新时重置分页并获取第一页数据
            fetchInitialFeedItems(StringsConstants.TABS[selectedTabIndex.value])

            isRefreshing.value = false
            // 刷新成功后显示提示信息。
            showSuccessMessage.value = true
            delay(IntegersConstants.SUCCESS_MESSAGE_DELAY) // 提示显示2秒。
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
            delay(IntegersConstants.REFRESH_DELAY) // 模拟网络延迟

            val newItems = MockRepo.getFeedItemsForTab(StringsConstants.TABS[selectedTabIndex.value], currentPage, pageSize)

            if (newItems.isNotEmpty()) {
                feedItems.value = feedItems.value + newItems
            } else {
                hasMoreData.value = false
            }

            isLoadingMore.value = false
        }
    }

    /**
     * 当用户长按某个信息流项目时调用，用于准备删除操作。
     *
     * @param item 被长按的信息流项目。
     */
    fun onDeleteItem(item: FeedItem) {
        itemToDelete.value = item
        showDeleteConfirmationDialog.value = true
    }

    /**
     * 确认删除信息流项目。
     */
    fun confirmDeleteItem() {
        itemToDelete.value?.let { item ->
            val currentItems = feedItems.value.toMutableList()
            currentItems.remove(item)
            feedItems.value = currentItems
        }
        // 重置状态
        resetDeleteState()
    }

    /**
     * 取消删除操作。
     */
    fun cancelDeleteItem() {
        // 重置状态
        resetDeleteState()
    }

    /**
     * 重置与删除相关的状态。
     */
    private fun resetDeleteState() {
        itemToDelete.value = null
        showDeleteConfirmationDialog.value = false
    }
}
