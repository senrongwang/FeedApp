package com.bytedance.feedapp.model

/**
 * 定义信息流中所有项目类型的基类。
 * 这是一个密封类，确保了在处理不同类型的卡片时可以进行详尽的编译时检查。
 */
sealed class FeedItem {
    /** 每个信息流项目的唯一标识符。*/
    abstract val id: String
    /** 用于反序列化时识别具体项目类型的字符串。*/
    abstract val type: String
    /** 每个信息流项目的排版方式。*/
    abstract val layout: String
}

/**
 * 纯文本信息流项目。
 *
 * @property id 唯一标识符。
 * @property text 显示的文本内容。
 * @property type 项目类型，默认为 "text"。
 * @property layout 排版方式。
 */
data class TextFeedItem(
    override val id: String,
    val text: String,
    override val type: String = "text",
    override val layout: String
) : FeedItem()

/**
 * 带图片和文本的信息流项目。
 *
 * @property id 唯一标识符。
 * @property imageUrl 图片的 URL。
 * @property text 图片附带的文本。
 * @property type 项目类型，默认为 "image"。
 * @property layout 排版方式。
 */
data class ImageFeedItem(
    override val id: String,
    val imageUrl: String,
    val text: String,
    override val type: String = "image",
    override val layout: String
) : FeedItem()

/**
 * 视频信息流项目。
 *
 * @property id 唯一标识符。
 * @property videoUrl 视频的 URL。
 * @property text 视频附带的文本。
 * @property type 项目类型，默认为 "video"。
 * @property layout 排版方式。
 */
data class VideoFeedItem(
    override val id: String,
    val videoUrl: String,
    val text: String,
    override val type: String = "video",
    override val layout: String
) : FeedItem()

/**
 * 产品信息流项目。
 *
 * @property id 唯一标识符。
 * @property imageUrl 产品的图片 URL。
 * @property name 产品名称。
 * @property price 产品价格。
 * @property type 项目类型，默认为 "product"。
 * @property layout 排版方式。
 */
data class ProductFeedItem(
    override val id: String,
    val imageUrl: String,
    val name: String,
    val price: String,
    override val type: String = "product",
    override val layout: String
) : FeedItem()
