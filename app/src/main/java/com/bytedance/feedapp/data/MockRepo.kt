package com.bytedance.feedapp.data

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * 一个模拟的数据仓库，负责从本地 JSON 文件加载和管理信息流数据。
 * 它实现了动态缓存策略：成功从网络（模拟）加载数据后会缓存到本地，
 * 当网络请求失败时，则会从缓存加载数据。
 */
object MockRepo {
    // 模拟的json文件
    private const val FILE_NAME = "feed_data.json"
    // 内部存储文件
    private const val CACHE_FILE_NAME = "feed_cache.json"

    // 使用 ConcurrentHashMap 提高线程安全性
    private var allFeedData: ConcurrentHashMap<String, MutableList<FeedItem>> = ConcurrentHashMap()

    /**
     * 加载并解析信息流数据。所有I/O和解析操作都在IO线程上执行。
     * 首先尝试从模拟的网络（assets/feed_data.json）加载，如果成功则缓存数据。
     * 如果失败，则尝试从本地缓存加载。
     * 如果两者都失败，则抛出异常。
     */
    suspend fun loadAndParseFeedData(context: Context) = withContext(Dispatchers.IO) {
        val jsonString = try {
            // 模拟网络请求
            val networkJson = context.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
            saveToCache(context, networkJson)
            Log.d("loadingLocal", "loading fileName: $FILE_NAME")
            networkJson
        } catch (e: IOException) {
            // 网络请求失败时，从缓存加载，如果缓存也失败则抛出异常
            loadFromCache(context) ?: throw IOException("网络加载失败")
        }

        // 清除旧数据并解析新数据
        allFeedData.clear()
        parseAndCacheData(jsonString)
    }

    /**
     * 根据标签页和分页参数获取信息流项目。
     */
    fun getFeedItemsForTab(tab: String, page: Int, pageSize: Int): List<FeedItem> {
        val itemsForTab = allFeedData[tab] ?: return emptyList()
        // 创建一个副本以避免在迭代时被修改
        val safeList = synchronized(itemsForTab) {
            itemsForTab.toList()
        }
        val startIndex = (page - 1) * pageSize
        if (startIndex >= safeList.size) {
            return emptyList()
        }
        val endIndex = (startIndex + pageSize).coerceAtMost(safeList.size)
        return safeList.subList(startIndex, endIndex)
    }

    /**
     * 从数据源中删除指定的信息流项目。
     */
    fun deleteFeedItem(item: FeedItem, tab: String) {
        allFeedData[tab]?.let {
            // 在同步块中操作列表保证线程安全
            synchronized(it) {
                it.removeAll { feedItem -> feedItem.id == item.id }
            }
        }
    }

    /**
     * 将 JSON 字符串解析并存入内存缓存。
     */
    private fun parseAndCacheData(jsonString: String) {
        val gson = GsonBuilder()
            .registerTypeAdapter(FeedItem::class.java, FeedItemDeserializer())
            .create()

        val type = object : TypeToken<Map<String, List<FeedItem>>>() {}.type
        val immutableMap: Map<String, List<FeedItem>> = gson.fromJson(jsonString, type)
        // toMutableList() 创建了新的列表，因此是安全的
        allFeedData.putAll(immutableMap.mapValues { it.value.toMutableList() })
    }

    /**
     * 将数据保存到应用的内部存储作为缓存。
     */
    private fun saveToCache(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, CACHE_FILE_NAME)
            file.writeText(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 从内部存储的缓存文件中加载数据。
     */
    private fun loadFromCache(context: Context): String? {
        return try {
            val file = File(context.filesDir, CACHE_FILE_NAME)
            Log.d("loadingLocal", "loading fileName: $CACHE_FILE_NAME")
            if (file.exists()) {
                file.readText()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
