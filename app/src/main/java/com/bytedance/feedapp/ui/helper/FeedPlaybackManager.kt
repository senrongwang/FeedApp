package com.bytedance.feedapp.ui.helper

import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bytedance.feedapp.constants.StringsConstants

private const val TAG = StringsConstants.EXPOSURE_TRACKER_TAG

/**
 * 视频播放管理器 (原 TestExposureCallback)
 * 职责：
 * 1. 记录所有卡片的曝光状态
 * 2. 决策当前应该播放哪个视频 (自动播放策略)
 */
class FeedPlaybackManager : ExposureCallback {
    // 状态 Map，UI层可能依然需要用它来显示调试信息，或者做其他逻辑
    val exposureState = mutableStateMapOf<Any, ExposureStatus>()

    // 当前正在播放的卡片 ID
    var playingCardId by mutableStateOf<Any?>(null)
        private set

    override fun onExposureStateChanged(cardId: Any, status: ExposureStatus, gridState: LazyStaggeredGridState) {
        // Log.d(TAG, "Card $cardId - status: $status")
        exposureState[cardId] = status

        // --- 核心自动播放策略 ---
        
        // 1. 找出所有完全可见的卡片
        val fullyVisibleCardIds = exposureState.filter { it.value == ExposureStatus.FULLY_VISIBLE }.keys

        // 2. 如果没有任何完全可见的卡片，停止播放
        if (fullyVisibleCardIds.isEmpty()) {
            if (playingCardId != null) {
                Log.d(TAG, "No fully visible cards. Stopping playback.")
                playingCardId = null
            }
            return
        }

        // 3. 从 GridState 中找到位置最靠上（Y轴偏移量最小）的完全可见卡片
        val topCardToPlay = gridState.layoutInfo.visibleItemsInfo
            .filter { it.key in fullyVisibleCardIds }
            .minByOrNull { it.offset.y }

        // 4. 更新播放状态
        val newPlayingCardId = topCardToPlay?.key
        if (playingCardId != newPlayingCardId) {
            Log.d(TAG, "Switching video playback. Old: $playingCardId, New: $newPlayingCardId")
            playingCardId = newPlayingCardId
        }
    }
}