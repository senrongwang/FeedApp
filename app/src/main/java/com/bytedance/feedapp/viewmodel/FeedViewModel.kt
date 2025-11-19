package com.bytedance.feedapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.feedapp.data.MockRepo
import com.bytedance.feedapp.model.FeedItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    val feedItems = mutableStateOf<List<FeedItem>>(emptyList())
    val isRefreshing = mutableStateOf(false)

    fun fetchFeedItemsForTab(tab: String) {
        feedItems.value = MockRepo.getFeedItemsForTab(tab)
    }

    fun refreshFeedItems(tab: String) {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(1500) // Simulate a network request
            feedItems.value = MockRepo.getFeedItemsForTab(tab)
            isRefreshing.value = false
        }
    }
}
