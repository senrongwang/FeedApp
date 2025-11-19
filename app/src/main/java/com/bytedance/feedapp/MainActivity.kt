package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.feedapp.constants.Strings
import com.bytedance.feedapp.ui.components.FeedList
import com.bytedance.feedapp.ui.components.FeedTabs
import com.bytedance.feedapp.ui.components.SearchBar
import com.bytedance.feedapp.ui.theme.FeedAppTheme
import com.bytedance.feedapp.viewmodel.FeedViewModel
import kotlinx.coroutines.delay

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
    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTabIndex) {
        feedViewModel.fetchFeedItemsForTab(Strings.TABS[selectedTabIndex])
    }

    val wasRefreshing = remember { mutableStateOf(isRefreshing) }
    LaunchedEffect(isRefreshing) {
        if (wasRefreshing.value && !isRefreshing) {
            showSuccessMessage = true
            delay(2000)
            showSuccessMessage = false
        }
        wasRefreshing.value = isRefreshing
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchBar(searchText = searchText, onSearchTextChange = { searchText = it })
            FeedTabs(selectedTabIndex = selectedTabIndex, onTabClick = { selectedTabIndex = it })
            FeedList(
                feedItems = feedItems,
                isRefreshing = isRefreshing,
                onRefresh = { feedViewModel.refreshFeedItems(Strings.TABS[selectedTabIndex]) }
            )
        }

        if (showSuccessMessage) {
            Text(
                text = Strings.REFRESH_INFO,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
