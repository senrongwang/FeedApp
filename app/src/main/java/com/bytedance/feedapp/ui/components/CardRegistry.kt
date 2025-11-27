package com.bytedance.feedapp.ui.components

import androidx.compose.runtime.Composable
import com.bytedance.feedapp.model.FeedItem

/**
 * 一个单例注册表，用于维护卡片类型（String）与其对应 Composable 函数之间的映射关系。
 * 这种设计模式实现了数据与视图的解耦，使卡片类型易于扩展。
 */
object CardRegistry {
    // 存储卡片类型 -> Composable 函数的映射, 添加了 playingCardId 参数
    private val cardViewRegistry = mutableMapOf<String, @Composable (FeedItem, (FeedItem) -> Unit, Any?) -> Unit>()

    /**
     * 注册一个新的卡片类型及其 Composable 实现。
     *
     * @param type 卡片类型的唯一标识符（如 "text"）。
     * @param card 用于渲染该类型卡片的 Composable 函数。
     */
    fun registerCard(type: String, card: @Composable (FeedItem, (FeedItem) -> Unit, Any?) -> Unit) {
        cardViewRegistry[type] = card
    }

    /**
     * 根据类型获取对应的 Composable 函数。
     *
     * @param type 要检索的卡片类型标识符。
     * @return 返回匹配的 Composable 函数，如果未注册则返回 `null`。
     */
    fun getCard(type: String): (@Composable (FeedItem, (FeedItem) -> Unit, Any?) -> Unit)? {
        return cardViewRegistry[type]
    }
}
