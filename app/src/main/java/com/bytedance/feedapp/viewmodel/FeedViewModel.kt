package com.bytedance.feedapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bytedance.feedapp.data.MockRepo
import com.bytedance.feedapp.model.FeedItem

class FeedViewModel : ViewModel() {
    val feedItems = mutableStateOf<List<FeedItem>>(emptyList())

    fun fetchFeedItemsForTab(tab: String) {
        feedItems.value = MockRepo.getFeedItemsForTab(tab)
    }
}
