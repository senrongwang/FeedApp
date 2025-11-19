package com.bytedance.feedapp.model

sealed class FeedItem {
    abstract val id: String
    abstract val type: String
}

data class TextFeedItem(
    override val id: String,
    val text: String,
    override val type: String = "text"
) : FeedItem()

data class ImageFeedItem(
    override val id: String,
    val imageUrl: String,
    val text: String,
    override val type: String = "image"
) : FeedItem()

data class VideoFeedItem(
    override val id: String,
    val videoUrl: String,
    val text: String,
    override val type: String = "video"
) : FeedItem()

data class ProductFeedItem(
    override val id: String,
    val imageUrl: String,
    val name: String,
    val price: String,
    override val type: String = "product"
) : FeedItem()

data class LoadingFeedItem(override val id: String = "-1", override val type: String = "loading") : FeedItem()
