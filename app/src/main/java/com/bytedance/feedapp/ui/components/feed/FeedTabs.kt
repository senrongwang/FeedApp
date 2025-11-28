package com.bytedance.feedapp.ui.components.feed

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bytedance.feedapp.constants.AppConstants

@Composable
fun FeedTabs(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    val tabs = AppConstants.TABS
    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabClick(index) },
                text = { Text(title) }
            )
        }
    }
}