package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.feedapp.model.ProductFeedItem

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