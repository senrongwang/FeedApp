package com.bytedance.feedapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.feedapp.model.FeedItem
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.LoadingFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedList(
    feedItems: List<FeedItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            state.startRefresh()
        } else {
            state.endRefresh()
        }
    }

    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
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

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state,
        )
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
