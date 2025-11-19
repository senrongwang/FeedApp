package com.bytedance.feedapp.ui.components.cards

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.model.TextFeedItem

@Composable
fun TextCard(item: TextFeedItem) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Text(text = item.text, modifier = Modifier.padding(16.dp))
    }
}