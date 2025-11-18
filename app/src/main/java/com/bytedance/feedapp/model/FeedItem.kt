package com.bytedance.feedapp.model

data class FeedItem(
    val id: Long,
    val type: String, // e.g., "text", "image", "video", "loading"
    val content: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val layout: String? = null
)
