package com.bytedance.feedapp.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.constants.AppConstants
import com.bytedance.feedapp.ui.helper.FeedPlaybackManager

/**
 * 曝光调试浮层 (原 ExposureTestTool)
 * 仅用于开发调试，显示当前的曝光状态列表
 */
@Composable
fun ExposureDebugOverlay(
    playbackManager: FeedPlaybackManager, // 依赖改名的 Manager
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = AppConstants.EXPOSURE_EVENT_TEXT,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // 显示列表状态
            LazyColumn {
                items(playbackManager.exposureState.toList()) { (cardId, status) ->
                    Text(
                        text = "Card $cardId: $status",
                        color = Color.White
                    )
                }
            }
        }
    }
}