package com.bytedance.feedapp.model

/**
 * `FeedItem` 是一个密封类，用作所有不同类型信息流项目的基类。
 * 这种设计允许我们在一个列表中处理多种信息流项目类型，同时保持类型安全。
 */
sealed class FeedItem {
    /**
     * `id` 是每个信息流项目的唯一标识符。
     */
    abstract val id: String
    /**
     * `type` 是用于标识信息流项目类型的字符串。
     */
    abstract val type: String
}

/**
 * `TextFeedItem` 表示一个纯文本信息流项目。
 *
 * @property id 唯一标识符。
 * @property text 要显示的文本内容。
 * @property type 信息流项目类型，默认为 "text"。
 */
data class TextFeedItem(
    override val id: String,
    val text: String,
    override val type: String = "text"
) : FeedItem()

/**
 * `ImageFeedItem` 表示一个带图片和文本的信息流项目。
 *
 * @property id 唯一标识符。
 * @property imageUrl 要显示的图片的 URL。
 * @property text 图片附带的文本。
 * @property type 信息流项目类型，默认为 "image"。
 */
data class ImageFeedItem(
    override val id: String,
    val imageUrl: String,
    val text: String,
    override val type: String = "image"
) : FeedItem()

/**
 * `VideoFeedItem` 表示一个视频信息流项目。
 *
 * @property id 唯一标识符。
 * @property videoUrl 视频的 URL。
 * @property text 视频附带的文本。
 * @property type 信息流项目类型，默认为 "video"。
 */
data class VideoFeedItem(
    override val id: String,
    val videoUrl: String,
    val text: String,
    override val type: String = "video"
) : FeedItem()

/**
 * `ProductFeedItem` 表示一个产品信息流项目。
 *
 * @property id 唯一标识符。
 * @property imageUrl 产品的图片 URL。
 * @property name 产品名称。
 * @property price 产品价格。
 * @property type 信息流项目类型，默认为 "product"。
 */
data class ProductFeedItem(
    override val id: String,
    val imageUrl: String,
    val name: String,
    val price: String,
    override val type: String = "product"
) : FeedItem()

/**
 * `LoadingFeedItem` 表示一个占位符项目，用于在加载更多数据时显示。
 *
 * @property id 唯一标识符，默认为 "-1"。
 * @property type 信息流项目类型，默认为 "loading"。
 */
data class LoadingFeedItem(override val id: String = "-1", override val type: String = "loading") : FeedItem()
