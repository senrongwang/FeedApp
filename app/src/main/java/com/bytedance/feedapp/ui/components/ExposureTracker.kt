package com.bytedance.feedapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.constants.StringsConstants
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.min

private const val TAG = StringsConstants.EXPOSURE_TRACKER_TAG

/**
 * 用于表示 LazyList 中项目曝光状态的枚举。
 * 表示项目的可见性状态。
 */
enum class ExposureStatus {
    /** 项目至少部分可见。 */
    VISIBLE,
    /** 项目至少 50% 可见。 */
    VISIBLE_50_PERCENT,
    /** 项目 100% 可见。 */
    FULLY_VISIBLE,
    /** 项目完全不可见。 */
    DISAPPEARED
}

/**
 * 曝光事件的监听器。
 * 实现此接口以在跟踪项目的曝光状态更改时接收回调。
 */
interface ExposureCallback {
    /**
     * 当卡片的曝光状态更改时调用。
     * @param cardId 卡片的唯一标识符。
     * @param status 新的曝光状态。
     */
    fun onExposureStateChanged(cardId: Any, status: ExposureStatus)
}

/**
 * [ExposureCallback] 的一个简单测试实现，用于记录曝光事件并将其存储以供显示。
 * 这对于调试和测试很有用。
 */
class TestExposureCallback : ExposureCallback {
    /**
     * 存储每个卡片最新曝光状态的 map。
     * 使用 `mutableStateMapOf` 以便在状态更改时自动触发 recomposition。
     */
    val exposureState = mutableStateMapOf<Any, ExposureStatus>()

    override fun onExposureStateChanged(cardId: Any, status: ExposureStatus) {
        Log.d(TAG, "Card $cardId - status: $status")
        exposureState[cardId] = status
    }
}

/**
 * 一个可组合函数，用于跟踪 [LazyStaggeredGridState] 中特定项目的曝光情况。
 * 它使用 [snapshotFlow] 来有效地观察布局信息的变化，并确定由 [cardId] 标识的项目的可见性。
 *
 * @param gridState 包含要跟踪的项目的网格的 [LazyStaggeredGridState]。
 * @param cardId 要跟踪的项目的唯一键，作为 `item` 或 `items` 的 `key` 参数提供。
 * @param callback 当曝光状态改变时要调用的 [ExposureCallback]。
 */
@Composable
fun TrackCardExposure(
    gridState: LazyStaggeredGridState,
    cardId: Any,
    callback: ExposureCallback
) {
    LaunchedEffect(gridState, cardId) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                val itemInfo = layoutInfo.visibleItemsInfo.find { it.key == cardId }
                if (itemInfo == null) {
                    ExposureStatus.DISAPPEARED
                } else {
                    val visiblePercentage = calculateVisiblePercentage(itemInfo, layoutInfo)
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
                callback.onExposureStateChanged(cardId, newStatus)
            }
    }
}

/**
 * 计算列表项在视口内的可见百分比。
 *
 * @param itemInfo 项目的 [LazyListItemInfo]。
 * @param layoutInfo 列表的 [androidx.compose.foundation.lazy.LazyListLayoutInfo]。
 * @return 项目的可见百分比，为 0.0 到 1.0 之间的浮点数。
 */
private fun calculateVisiblePercentage(
    itemInfo: LazyListItemInfo,
    layoutInfo: androidx.compose.foundation.lazy.LazyListLayoutInfo
): Float {
    // 可见区域（视口）的开始和结束偏移量。
    val viewportStartOffset = layoutInfo.viewportStartOffset
    val viewportEndOffset = layoutInfo.viewportEndOffset

    // 项目的开始和结束偏移量。
    val itemStartOffset = itemInfo.offset
    val itemEndOffset = itemInfo.offset + itemInfo.size

    // 计算项目和视口之间的交集。
    val visibleStart = max(itemStartOffset, viewportStartOffset)
    val visibleEnd = min(itemEndOffset, viewportEndOffset)

    // 项目可见部分的高度。
    val visibleHeight = (visibleEnd - visibleStart).coerceAtLeast(0)

    // 计算可见性百分比。
    return if (itemInfo.size > 0) visibleHeight.toFloat() / itemInfo.size.toFloat() else 0f
}

/**
 * 计算网格项在视口内的可见百分比。
 *
 * @param itemInfo 项目的 [LazyStaggeredGridItemInfo]。
 * @param layoutInfo 网格的 [LazyStaggeredGridLayoutInfo]。
 * @return 项目的可见百分比，为 0.0 到 1.0 之间的浮点数。
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

/**
 * 一个用于在应用内显示曝光事件的测试工具。
 * 这个可组合函数会在屏幕右下角显示一个半透明的框，
 * 其中包含由 [TestExposureCallback] 捕获的曝光事件。
 *
 * @param testExposureCallback [TestExposureCallback] 的实例，用于存储和提供曝光状态。
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun ExposureTestTool(
    testExposureCallback: TestExposureCallback,
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
                text = StringsConstants.EXPOSURE_EVENT_TEXT,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn {
                items(testExposureCallback.exposureState.toList()) { (cardId, status) ->
                    Text(
                        text = "Card $cardId: $status",
                        color = Color.White
                    )
                }
            }
        }
    }
}
