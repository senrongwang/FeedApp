package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.model.TextFeedItem

/**
 * `TextCard` 是一个 Composable 函数，用于显示纯文本信息流项目。
 *
 * @param item 要显示的 `TextFeedItem` 数据。
 * @param onLongPress 用户长按卡片时调用的回调函数。
 */
@Composable
fun TextCard(item: TextFeedItem, onLongPress: (TextFeedItem) -> Unit) {
    // `Card` 可组合项为文本信息流项目提供了一个 Material Design 卡片容器。
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress(item)
                    }
                )
            }
    ) {
        // `Text` 可组合项显示 `TextFeedItem` 中包含的文本。
        Text(text = item.text, modifier = Modifier.padding(16.dp))
    }
}