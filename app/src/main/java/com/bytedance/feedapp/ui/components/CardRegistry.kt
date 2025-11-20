package com.bytedance.feedapp.ui.components

import androidx.compose.runtime.Composable
import com.bytedance.feedapp.model.FeedItem

/**
 * `CardRegistry` 是一个单例对象，用于维护一个从卡片类型到其对应 Composable 函数的注册表。
 * 这种方法允许在运行时动态注册和检索卡片视图，从而实现插件式体系结构。
 */
object CardRegistry {
    // `cardViewRegistry` 是一个私有可变映射，用于存储卡片类型（字符串）与其对应的 Composable 函数之间的关系。
    private val cardViewRegistry = mutableMapOf<String, @Composable (FeedItem, (FeedItem) -> Unit) -> Unit>()

    /**
     * `registerCard` 函数用于向注册表中添加一个新的卡片类型及其 Composable 函数。
     *
     * @param type 卡片类型的唯一标识符（例如，“text”、“image”）。
     * @param card 与该类型关联的 Composable 函数。
     */
    fun registerCard(type: String, card: @Composable (FeedItem, (FeedItem) -> Unit) -> Unit) {
        cardViewRegistry[type] = card
    }

    /**
     * `getCard` 函数用于根据给定的卡片类型从注册表中检索 Composable 函数。
     *
     * @param type 要检索的卡片类型的标识符。
     * @return 如果找到了对应类型的 Composable 函数，则返回它；否则返回 `null`。
     */
    fun getCard(type: String): (@Composable (FeedItem, (FeedItem) -> Unit) -> Unit)? {
        return cardViewRegistry[type]
    }
}