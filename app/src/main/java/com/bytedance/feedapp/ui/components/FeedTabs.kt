package com.bytedance.feedapp.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bytedance.feedapp.constants.StringsConstants

@Composable
fun FeedTabs(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    val tabs = StringsConstants.TABS
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
