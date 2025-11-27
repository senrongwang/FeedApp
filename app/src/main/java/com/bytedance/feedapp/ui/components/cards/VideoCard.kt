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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.feedapp.model.VideoFeedItem
import kotlinx.coroutines.delay


/**
 * `VideoCard` 是一个 Composable 函数，用于显示视频信息流项目。
 *
 * @param item 要显示的 `VideoFeedItem` 数据。
 * @param onLongPress 用户长按卡片时调用的回调函数。
 * @param isPlaying 表示视频当前是否正在播放的布尔值。
 */
@Composable
fun VideoCard(item: VideoFeedItem, onLongPress: (VideoFeedItem) -> Unit, isPlaying: Boolean) {
    var countdown by remember { mutableStateOf(5) }

    // 当这个视频卡是正在播放的视频时，启动一个倒计时。
    // 这是一个模拟真实视频播放的简化方案。
    if (isPlaying) {
        // `LaunchedEffect` 用于在 Composable 的生命周期内运行挂起函数。
        // 当 `isPlaying` 或 `item.id` 改变时，这个 effect 会重新启动。
        androidx.compose.runtime.LaunchedEffect(key1 = isPlaying, key2 = item.id) {
            countdown = 5
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures {
                    onLongPress(item)
                }
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    // 如果视频正在播放，显示倒计时。
                    Text(
                        text = "模拟播放中: $countdown s",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                } else {
                    // 如果视频未播放，显示播放图标。
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = "Play Video",
                        tint = Color.White
                    )
                }
            }
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}
