package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.model.VideoFeedItem

/**
 * `VideoCard` 是一个 Composable 函数，用于显示视频信息流项目。
 *
 * @param item 要显示的 `VideoFeedItem` 数据。
 * @param onLongPress 用户长按卡片时调用的回调函数。
 */
@Composable
fun VideoCard(item: VideoFeedItem, onLongPress: (VideoFeedItem) -> Unit) {
    // `Card` 可组合项为视频信息流项目提供了一个 Material Design 卡片容器。
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
            // `Box` 可组合项用作视频预览的占位符。
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 填充父项的整个宽度。
                    .height(200.dp) // 设置一个固定的高度。
                    .background(Color.Black), // 设置一个黑色背景。
                contentAlignment = Alignment.Center // 将子项在 `Box` 内居中对齐。
            ) {
                // `Icon` 可组合项显示一个播放图标，表示这是一个视频。
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play), // 使用安卓内置的播放图标。
                    contentDescription = "Play Video", // 为无障碍功能提供描述。
                    tint = Color.White // 将图标颜色设置为白色，以便在黑色背景上可见。
                )
            }
            // `Text` 可组合项显示视频的附带文本。
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}