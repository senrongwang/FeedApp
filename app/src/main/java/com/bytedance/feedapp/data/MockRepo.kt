package com.bytedance.feedapp.data

import android.content.Context
import com.bytedance.feedapp.constants.StringsConstants
import com.bytedance.feedapp.model.FeedItem
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * 一个模拟的数据仓库，负责从本地 JSON 文件加载和管理信息流数据。
 */
object MockRepo {

    // 内存缓存，存储所有信息流数据，设为可变以支持删除。
    private var allFeedData: MutableMap<String, MutableList<FeedItem>> = mutableMapOf()

    /**
     * 从 `assets/feed_data.json` 加载并解析数据到内存缓存中。
     * 如果数据已加载，则此函数不执行任何操作。
     */
    fun loadAndParseFeedData(context: Context) {
        if (allFeedData.isNotEmpty()) return

        val jsonString = context.assets.open("feed_data.json").bufferedReader().use { it.readText() }

        val gson = GsonBuilder()
            .registerTypeAdapter(FeedItem::class.java, FeedItemDeserializer())
            .create()

        val type = object : TypeToken<Map<String, List<FeedItem>>>() {}.type
        val immutableMap: Map<String, List<FeedItem>> = gson.fromJson(jsonString, type)
        // 将解析的数据转为可变集合，以便后续操作
        allFeedData = immutableMap.mapValues { it.value.toMutableList() }.toMutableMap()
    }

    /**
     * 根据标签页和分页参数获取信息流项目。
     */
    fun getFeedItemsForTab(tab: String, page: Int, pageSize: Int): List<FeedItem> {
        val itemsForTab = allFeedData[tab] ?: return emptyList()
        val startIndex = (page - 1) * pageSize
        if (startIndex >= itemsForTab.size) {
            return emptyList()
        }
        val endIndex = (startIndex + pageSize).coerceAtMost(itemsForTab.size)
        return itemsForTab.subList(startIndex, endIndex).toList()
    }

    /**
     * 从数据源中删除指定的信息流项目。
     */
    fun deleteFeedItem(item: FeedItem, tab: String) {
        allFeedData[tab]?.removeAll { it.id == item.id }
    }
}

/**
 * 自定义的 Gson 反序列化器，用于处理 `FeedItem` 的多态性。
 * 它会根据 JSON 中的 `type` 字段，将对象解析为正确的 `FeedItem` 子类。
 */
class FeedItemDeserializer : JsonDeserializer<FeedItem> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeedItem? {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        if (!jsonObject.has("layout")) {
            jsonObject.addProperty("layout", StringsConstants.DEFAULT_FEED_TIEM_LAYOUT)
        }

        return when (type) {
            "text" -> context.deserialize(jsonObject, TextFeedItem::class.java)
            "image" -> context.deserialize(jsonObject, ImageFeedItem::class.java)
            "video" -> context.deserialize(jsonObject, VideoFeedItem::class.java)
            "product" -> context.deserialize(jsonObject, ProductFeedItem::class.java)
            else -> null
        }
    }
}
