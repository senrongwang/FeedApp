package com.bytedance.feedapp.model

sealed class FeedItem {
    abstract val id: String
}

data class TextFeedItem(
    override val id: String,
    val text: String
) : FeedItem()

data class ImageFeedItem(
    override val id: String,
    val imageUrl: String,
    val text: String
) : FeedItem()

data class VideoFeedItem(
    override val id: String,
    val videoUrl: String,
    val text: String
) : FeedItem()

data class LoadingFeedItem(override val id: String = "-1") : FeedItem()
