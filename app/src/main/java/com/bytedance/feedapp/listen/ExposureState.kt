package com.bytedance.feedapp.listen

data class ExposureState(
    var isExposed: Boolean = false,
    var is50PercentExposed: Boolean = false,
    var isFullyExposed: Boolean = false,
    var isDisappeared: Boolean = true // Initially, all cards are considered not visible
)
