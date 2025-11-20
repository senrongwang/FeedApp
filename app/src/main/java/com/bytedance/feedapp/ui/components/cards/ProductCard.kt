package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.feedapp.model.ProductFeedItem

/**
 * `ProductCard` 是一个 Composable 函数，用于显示产品信息流项目，包括图片、名称和价格。
 *
 * @param item 要显示的 `ProductFeedItem` 数据。
 * @param onLongPress 用户长按卡片时调用的回调函数。
 */
@Composable
fun ProductCard(item: ProductFeedItem, onLongPress: (ProductFeedItem) -> Unit) {
    // `Card` 可组合项为产品信息流项目提供了一个 Material Design 卡片容器。
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
        // `Column` 可组合项垂直排列其子项。
        Column {
            // `AsyncImage` 用于异步加载和显示产品图片。
            AsyncImage(
                model = item.imageUrl, // 产品的图片 URL。
                contentDescription = null, // 内容描述为 null，因为图片是装饰性的。
                modifier = Modifier.fillMaxWidth() // 使图片填充卡片的整个宽度。
            )
            // `Row` 可组合项水平排列其子项。
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                // `Text` 可组合项显示产品名称。
                Text(text = item.name, modifier = Modifier.weight(1f)) // 使用 `weight` 来占据尽可能多的空间。
                // `Text` 可组合项显示产品价格。
                Text(text = item.price, color = Color.Red) // 将价格文本颜色设置为红色以突出显示。
            }
        }
    }
}