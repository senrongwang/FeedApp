package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.feedapp.model.ImageFeedItem

/**
 * `ImageCard` 是一个 Composable 函数，用于显示带图片和文本的信息流项目。
 *
 * @param item 要显示的 `ImageFeedItem` 数据。
 */
@Composable
fun ImageCard(item: ImageFeedItem) {
    // `Card` 可组合项为图片信息流项目提供了一个 Material Design 卡片容器。
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        // `Column` 可组合项垂直排列其子项。
        Column {
            // `AsyncImage` 是一个来自 Coil 库的 Composable 函数，用于异步加载和显示图片。
            AsyncImage(
                model = item.imageUrl, // 要加载的图片的 URL。
                contentDescription = null, // 由于图片是装饰性的，因此内容描述为 null。
                modifier = Modifier.fillMaxWidth() // 使图片填充卡片的整个宽度。
            )
            // `Text` 可组合项显示图片附带的文本。
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}