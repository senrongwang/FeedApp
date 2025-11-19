package com.bytedance.feedapp.data

import android.content.Context
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

object MockRepo {

    private var allFeedData: Map<String, List<FeedItem>> = emptyMap()

    fun loadAndParseFeedData(context: Context) {
        if (allFeedData.isNotEmpty()) return

        val jsonString = context.assets.open("feed_data.json").bufferedReader().use { it.readText() }

        val gson = GsonBuilder()
            .registerTypeAdapter(FeedItem::class.java, FeedItemDeserializer())
            .create()

        val type = object : TypeToken<Map<String, List<FeedItem>>>() {}.type
        allFeedData = gson.fromJson(jsonString, type)
    }

    fun getFeedItemsForTab(tab: String, page: Int, pageSize: Int): List<FeedItem> {
        val itemsForTab = allFeedData[tab] ?: return emptyList()
        val startIndex = (page - 1) * pageSize
        if (startIndex >= itemsForTab.size) {
            return emptyList()
        }
        val endIndex = (startIndex + pageSize).coerceAtMost(itemsForTab.size)
        return itemsForTab.subList(startIndex, endIndex)
    }
}

class FeedItemDeserializer : JsonDeserializer<FeedItem> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeedItem? {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        return when (type) {
            "text" -> context.deserialize(jsonObject, TextFeedItem::class.java)
            "image" -> context.deserialize(jsonObject, ImageFeedItem::class.java)
            "video" -> context.deserialize(jsonObject, VideoFeedItem::class.java)
            "product" -> context.deserialize(jsonObject, ProductFeedItem::class.java)
            else -> null
        }
    }
}
