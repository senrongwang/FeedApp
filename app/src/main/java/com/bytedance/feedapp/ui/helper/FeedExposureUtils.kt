package com.bytedance.feedapp.ui.helper

import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.bytedance.feedapp.constants.AppConstants
import com.bytedance.feedapp.model.FeedItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.min

private const val TAG = AppConstants.EXPOSURE_TRACKER_TAG

/**
 * 曝光状态枚举
 */
enum class ExposureStatus {
    VISIBLE,
    VISIBLE_50_PERCENT,
    FULLY_VISIBLE,
    DISAPPEARED
}

/**
 * 曝光回调接口
 */
interface ExposureCallback {
    fun onExposureStateChanged(item: FeedItem, status: ExposureStatus, gridState: LazyStaggeredGridState)
}

/**
 * 核心逻辑组件：监听滚动并计算曝光
 */
@Composable
fun TrackCardExposure(
    gridState: LazyStaggeredGridState,
    item: FeedItem,
    callback: ExposureCallback
) {
    LaunchedEffect(gridState, item.id) {
        snapshotFlow { gridState.layoutInfo to gridState.firstVisibleItemScrollOffset }
            .map { (layoutInfo, _) ->
                val itemInfo = layoutInfo.visibleItemsInfo.find { it.key == item.id }
                if (itemInfo == null) {
                    ExposureStatus.DISAPPEARED
                } else {
                    val visiblePercentage = calculateVisiblePercentage(itemInfo, layoutInfo)
                    Log.d(TAG, "cardId: ${item.id}, percentage: $visiblePercentage") // 根据需要开启日志
                    when {
                        visiblePercentage >= 1.0f -> ExposureStatus.FULLY_VISIBLE
                        visiblePercentage >= 0.5f -> ExposureStatus.VISIBLE_50_PERCENT
                        visiblePercentage > 0f -> ExposureStatus.VISIBLE
                        else -> ExposureStatus.DISAPPEARED
                    }
                }
            }
            .distinctUntilChanged()
            .collect { newStatus ->
                callback.onExposureStateChanged(item, newStatus, gridState)
            }
    }
}

/**
 * 纯数学计算逻辑
 */
private fun calculateVisiblePercentage(
    itemInfo: LazyStaggeredGridItemInfo,
    layoutInfo: LazyStaggeredGridLayoutInfo
): Float {
    val viewportStartOffset = layoutInfo.viewportStartOffset
    val viewportEndOffset = layoutInfo.viewportEndOffset
    val itemStartOffset = itemInfo.offset.y
    val itemEndOffset = itemInfo.offset.y + itemInfo.size.height

    val visibleStart = max(itemStartOffset, viewportStartOffset)
    val visibleEnd = min(itemEndOffset, viewportEndOffset)
    val visibleHeight = (visibleEnd - visibleStart).coerceAtLeast(0)

    return if (itemInfo.size.height > 0) visibleHeight.toFloat() / itemInfo.size.height.toFloat() else 0f
}