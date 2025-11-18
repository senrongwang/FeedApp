package com.bytedance.feedapp.listen

interface ExposureListener {
    fun onCardExposed(position: Int)
    fun onCardExposed50Percent(position: Int)
    fun onCardFullyExposed(position: Int)
    fun onCardDisappeared(position: Int)
}
