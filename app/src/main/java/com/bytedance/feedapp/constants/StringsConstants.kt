package com.bytedance.feedapp.constants

/**
 * 存放应用中使用的字符串常量，便于管理和国际化。
 */
object StringsConstants {
    // --- 搜索栏 ---
    const val SEARCH_TEXT_PLACEHOLDER = "点击输入搜索内容"
    const val BACK_BUTTON_CONTENT_DESCRIPTION = "Back"
    const val SEARCH_BUTTON_TEXT = "搜索"

    // --- 标签页 ---
    val TABS = listOf("综合", "视频", "用户", "图文", "商品")

    // --- 状态提示 ---
    const val REFRESH_INFO = "刷新成功"
    const val LOADING_MORE = "正在加载..."
    const val NO_MORE_DATA = "没有更多内容了"

    // --- 删除确认对话框 ---
    const val DELETE_CONFIRMATION_TITLE = "确认删除"
    const val DELETE_CONFIRMATION_MESSAGE = "您确定要删除这张卡片吗？"
    const val CONFIRM = "确认"
    const val CANCEL = "取消"

    // --- 卡片曝光tag
    const val EXPOSURE_TRACKER_TAG = "ExposureTracker"
}
