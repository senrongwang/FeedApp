package com.bytedance.feedapp.listen

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExposureManager(
    private val recyclerView: RecyclerView,
    private val listener: ExposureListener
) {
    private val exposureStates = mutableMapOf<Int, ExposureState>()
    private val layoutManager = recyclerView.layoutManager as LinearLayoutManager

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            checkExposure()
        }
    }

    private fun checkExposure() {
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        if (firstVisiblePosition == -1 || lastVisiblePosition == -1) return

        // Check items that are currently visible
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            val state = exposureStates.getOrPut(i) { ExposureState() }

            val visiblePercentage = getVisibleHeightPercentage(view)

            // 1. Card Exposed
            if (visiblePercentage > 0 && !state.isExposed) {
                state.isExposed = true
                state.isDisappeared = false
                listener.onCardExposed(i)
            }

            // 2. Card Exposed 50%
            if (visiblePercentage >= 50 && !state.is50PercentExposed) {
                state.is50PercentExposed = true
                listener.onCardExposed50Percent(i)
            }

            // 3. Card Fully Exposed
            if (visiblePercentage == 100 && !state.isFullyExposed) {
                state.isFullyExposed = true
                listener.onCardFullyExposed(i)
            }
        }

        // Check items that are no longer visible
        val positionsToCheck = exposureStates.keys.toList()
        for (position in positionsToCheck) {
            if (position < firstVisiblePosition || position > lastVisiblePosition) {
                val state = exposureStates[position] ?: continue
                if (!state.isDisappeared) {
                    state.isDisappeared = true
                    state.isExposed = false
                    state.is50PercentExposed = false
                    state.isFullyExposed = false
                    listener.onCardDisappeared(position)
                }
            }
        }
    }

    private fun getVisibleHeightPercentage(view: View): Int {
        val itemRect = Rect()
        val isVisible = view.getGlobalVisibleRect(itemRect)

        if (!isVisible) {
            return 0
        }

        val visibleHeight = itemRect.height()
        val totalHeight = view.height

        return if (totalHeight > 0) (visibleHeight * 100) / totalHeight else 0
    }

    fun startListening() {
        recyclerView.addOnScrollListener(scrollListener)
        // Initial check
        recyclerView.post { checkExposure() }
    }

    fun stopListening() {
        recyclerView.removeOnScrollListener(scrollListener)
    }
}
