package com.bytedance.feedapp.data

import com.bytedance.feedapp.model.FeedItem
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem

object MockRepo {

    fun getFeedItemsForTab(tab: String): List<FeedItem> {
        return when (tab) {
            "综合" -> getMixedFeedItems()
            "视频" -> getVideoFeedItems()
            "用户" -> getUserFeedItems()
            "图文" -> getImageFeedItems()
            "商品" -> getProductFeedItems()
            else -> emptyList()
        }
    }

    private fun getMixedFeedItems(): List<FeedItem> {
        return listOf(
            TextFeedItem(id = "1", text = "This is a text-only feed item for the '综合' tab."),
            ImageFeedItem(id = "2", imageUrl = "https://pic.nximg.cn/file/20230308/34543442_165914838100_2.jpg", text = "An image for the '综合' tab."),
            VideoFeedItem(id = "3", videoUrl = "", text = "A video for the '综合' tab."),
            ProductFeedItem(id = "4", imageUrl = "https://pic.nximg.cn/file/20230308/34543442_165914838100_2.jpg", name = "Awesome Product", price = "$99.99")
        )
    }

    private fun getVideoFeedItems(): List<FeedItem> {
        return listOf(
            VideoFeedItem(id = "v1", videoUrl = "https://ts3.tc.mm.bing.net/th/id/OVP.cu7dO4nvlSCaZNxbiYgLwwIIFE?w=243&h=136&c=7&rs=1&qlt=70&o=7&pid=2.1&rm=3", text = "This is a video."),
            VideoFeedItem(id = "v2", videoUrl = "https://www.bilibili.com/video/av67907933/", text = "Another video here.")
        )
    }

    private fun getUserFeedItems(): List<FeedItem> {
        return listOf(
            TextFeedItem(id = "u1", text = "This is a user's post."),
            TextFeedItem(id = "u2", text = "Another user update.")
        )
    }

    private fun getImageFeedItems(): List<FeedItem> {
        return listOf(
            ImageFeedItem(id = "i1", imageUrl = "https://n.sinaimg.cn/sinacn10121/489/w1809h1080/20190411/b868-hvntnkq8059577.jpg", text = "Just an image."),
            ImageFeedItem(id = "i2", imageUrl = "https://pic.nximg.cn/file/20230308/34543442_165914838100_2.jpg", text = "One more image.")
        )
    }

    private fun getProductFeedItems(): List<FeedItem> {
        return listOf(
            ProductFeedItem(id = "p1", imageUrl = "https://n.sinaimg.cn/sinacn10121/489/w1809h1080/20190411/b868-hvntnkq8059577.jpg", name = "Product A", price = "$19.99"),
            ProductFeedItem(id = "p2", imageUrl = "https://n.sinaimg.cn/sinacn10121/489/w1809h1080/20190411/b868-hvntnkq8059577.jpg0", name = "Product B", price = "$29.99")
        )
    }
}
