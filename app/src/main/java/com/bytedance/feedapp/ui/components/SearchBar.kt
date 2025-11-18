package com.bytedance.feedapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytedance.feedapp.R
import com.bytedance.feedapp.constants.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle back press */ }) {
            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24), contentDescription = Strings.BACK_BUTTON_CONTENT_DESCRIPTION)
        }
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(Strings.SEARCH_TEXT_PLACEHOLDER) }
        )
        TextButton(onClick = { /* Handle search */ }) {
            Text(Strings.SEARCH_BUTTON_TEXT, color = Color.Red)
        }
    }
}
