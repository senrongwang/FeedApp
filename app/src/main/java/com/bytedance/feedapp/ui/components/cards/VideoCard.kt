package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.model.VideoFeedItem

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