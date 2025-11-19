package com.bytedance.feedapp.ui.components

import androidx.compose.runtime.Composable
import com.bytedance.feedapp.model.FeedItem

object CardRegistry {
    private val cardViewRegistry = mutableMapOf<String, @Composable (FeedItem) -> Unit>()

    fun registerCard(type: String, card: @Composable (FeedItem) -> Unit) {
        cardViewRegistry[type] = card
    }

    fun getCard(type: String): (@Composable (FeedItem) -> Unit)? {
        return cardViewRegistry[type]
    }
}