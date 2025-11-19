package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.feedapp.constants.Strings
import com.bytedance.feedapp.ui.components.FeedList
import com.bytedance.feedapp.ui.components.FeedTabs
import com.bytedance.feedapp.ui.components.SearchBar
import com.bytedance.feedapp.ui.theme.FeedAppTheme
import com.bytedance.feedapp.viewmodel.FeedViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FeedApp()
                }
            }
        }
    }
}

@Composable
fun FeedApp(feedViewModel: FeedViewModel = viewModel()) {
    var searchText by remember { mutableStateOf(Strings.SEARCH_TEXT_PLACEHOLDER) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val feedItems by feedViewModel.feedItems
    val isRefreshing by feedViewModel.isRefreshing

    LaunchedEffect(selectedTabIndex) {
        feedViewModel.fetchFeedItemsForTab(Strings.TABS[selectedTabIndex])
    }

    Column {
        SearchBar(searchText = searchText, onSearchTextChange = { searchText = it })
        FeedTabs(selectedTabIndex = selectedTabIndex, onTabClick = { selectedTabIndex = it })
        FeedList(
            feedItems = feedItems,
            isRefreshing = isRefreshing,
            onRefresh = { feedViewModel.refreshFeedItems(Strings.TABS[selectedTabIndex]) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
