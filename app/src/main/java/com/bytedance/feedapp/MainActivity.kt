package com.bytedance.feedapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.bytedance.feedapp.ui.theme.FeedAppTheme

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
fun FeedApp() {
    var searchText by remember { mutableStateOf("点击输入搜索内容") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("综合", "视频", "用户", "图文", "商品")

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back press */ }) {
                Icon(painter = painterResource(id = android.R.drawable.ic_media_previous), contentDescription = "Back")
            }
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("点击输入搜索内容") }
            )
            TextButton(onClick = { /* Handle search */ }) {
                Text("搜索", color = Color.Red)
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

        // The content below the tabs (RecyclerView and SwipeRefreshLayout)
        // would be implemented here as a LazyColumn with pull-to-refresh.
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedAppTheme {
        FeedApp()
    }
}
