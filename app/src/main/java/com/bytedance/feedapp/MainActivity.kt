package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bytedance.feedapp.constants.Strings
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.LoadingFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedApp(feedViewModel: FeedViewModel = viewModel()) {
    var searchText by remember { mutableStateOf(Strings.SEARCH_TEXT_PLACEHOLDER) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = Strings.TABS
    val feedItems by feedViewModel.feedItems

    LaunchedEffect(selectedTabIndex) {
        feedViewModel.fetchFeedItemsForTab(tabs[selectedTabIndex])
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back press */ }) {
                Icon(painter = painterResource(id = android.R.drawable.ic_media_previous), contentDescription = Strings.BACK_BUTTON_CONTENT_DESCRIPTION)
            }
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(Strings.SEARCH_TEXT_PLACEHOLDER) }
            )
            TextButton(onClick = { /* Handle search */ }) {
                Text(Strings.SEARCH_BUTTON_TEXT, color = Color.Red)
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(feedItems) { item ->
                when (item) {
                    is TextFeedItem -> TextCard(item)
                    is ImageFeedItem -> ImageCard(item)
                    is VideoFeedItem -> VideoCard(item)
                    is ProductFeedItem -> ProductCard(item)
                    is LoadingFeedItem -> LoadingCard()
                }
            }
        }
    }
}

@Composable
fun TextCard(item: TextFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Text(text = item.text, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ImageCard(item: ImageFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun VideoCard(item: VideoFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Play Video",
                    tint = Color.White
                )
            }
            Text(text = item.text, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ProductCard(item: ProductFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name, modifier = Modifier.weight(1f))
                Text(text = item.price, color = Color.Red)
            }
        }
    }
}

@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
