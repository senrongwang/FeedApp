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
 * `FeedViewModel` 负责管理信息流的业务逻辑和UI状态。
 * 它从 `MockRepo` 获取数据，并处理用户交互，如刷新、加载更多和删除。
 */
class FeedViewModel(application: Application) : AndroidViewModel(application) {
    // UI 状态：信息流列表
    val feedItems = mutableStateOf<List<FeedItem>>(emptyList())
    // UI 状态：是否正在刷新
    val isRefreshing = mutableStateOf(false)
    // UI 状态：当前选中的标签页
    val selectedTabIndex = mutableStateOf(0)
    // UI 状态：显示成功消息
    val showSuccessMessage = mutableStateOf(false)
    // UI 状态：是否正在加载更多
    val isLoadingMore = mutableStateOf(false)
    // 数据状态：是否还有更多数据
    val hasMoreData = mutableStateOf(true)
    // UI 状态：显示删除确认对话框
    val showDeleteConfirmationDialog = mutableStateOf(false)
    // 状态：待删除的项目
    val itemToDelete = mutableStateOf<FeedItem?>(null)

    private var currentPage = 1
    private val pageSize = 5

    init {
        loadInitialData()
    }

    /** 当用户切换标签页时调用。*/
    fun onTabSelected(index: Int) {
        selectedTabIndex.value = index
        fetchInitialFeedItems(StringsConstants.TABS[index])
    }

    /** 执行下拉刷新操作。*/
    fun refreshFeedItems() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(IntegersConstants.REFRESH_DELAY)

            // 重新加载数据，MockRepo 会处理网络和缓存
            MockRepo.loadAndParseFeedData(getApplication())
            fetchInitialFeedItems(StringsConstants.TABS[selectedTabIndex.value])

            isRefreshing.value = false
            showSuccessMessage.value = true
            delay(IntegersConstants.SUCCESS_MESSAGE_DELAY)
            showSuccessMessage.value = false
        }
    }

    /** 加载下一页的数据。*/
    fun loadMoreFeedItems() {
        if (isLoadingMore.value || !hasMoreData.value) return
        isLoadingMore.value = true
        viewModelScope.launch {
            currentPage++
            delay(IntegersConstants.REFRESH_DELAY)

            // 从内存中获取下一页数据
            val newItems = MockRepo.getFeedItemsForTab(StringsConstants.TABS[selectedTabIndex.value], currentPage, pageSize)

            if (newItems.isNotEmpty()) {
                val currentItems = feedItems.value
                val distinctNewItems = newItems.filter { newItem -> currentItems.none { it.id == newItem.id } }
                feedItems.value = currentItems + distinctNewItems
            } else {
                hasMoreData.value = false
            }

            isLoadingMore.value = false
        }
    }

    /** 准备删除操作，显示确认对话框。*/
    fun onDeleteItem(item: FeedItem) {
        itemToDelete.value = item
        showDeleteConfirmationDialog.value = true
    }

    /** 在用户确认后执行删除操作。*/
    fun confirmDeleteItem() {
        itemToDelete.value?.let { itemToRemove ->
            // 从数据源和UI列表中同时移除
            MockRepo.deleteFeedItem(itemToRemove, StringsConstants.TABS[selectedTabIndex.value])
            feedItems.value = feedItems.value.filter { it.id != itemToRemove.id }
        }
        resetDeleteState()
    }

    /** 取消删除操作。*/
    fun cancelDeleteItem() {
        resetDeleteState()
    }

    /** 获取初始数据并重置分页。*/
    private fun fetchInitialFeedItems(tab: String) {
        currentPage = 1
        hasMoreData.value = true
        feedItems.value = MockRepo.getFeedItemsForTab(tab, currentPage, pageSize)
    }

    /** 重置与删除相关的状态。*/
    private fun resetDeleteState() {
        itemToDelete.value = null
        showDeleteConfirmationDialog.value = false
    }

    /** 加载初始数据，由 MockRepo 处理缓存逻辑。*/
    private fun loadInitialData() {
        MockRepo.loadAndParseFeedData(getApplication())
        fetchInitialFeedItems(StringsConstants.TABS[selectedTabIndex.value])
    }
}
