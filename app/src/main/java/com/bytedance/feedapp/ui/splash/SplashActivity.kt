package com.bytedance.feedapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bytedance.feedapp.MainActivity
import com.bytedance.feedapp.constants.IntegersConstants
import com.bytedance.feedapp.ui.theme.FeedAppTheme
import com.bytedance.feedapp.ui.theme.GeminiBlueEnd
import com.bytedance.feedapp.ui.theme.GeminiBlueStart
import com.bytedance.feedapp.ui.theme.GeminiTextEnd
import com.bytedance.feedapp.ui.theme.GeminiTextStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make it full-screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            FeedAppTheme {
                SplashScreen()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(IntegersConstants.REFRESH_DELAY)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}

@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GeminiBlueStart,
                        GeminiBlueEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val textBrush = Brush.linearGradient(
            colors = listOf(
                GeminiTextStart,
                GeminiTextEnd
            )
        )

        Text(
            text = "FeedApp",
            style = MaterialTheme.typography.headlineLarge.copy(
                brush = textBrush
            ),
            modifier = Modifier.scale(scale)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    FeedAppTheme {
        SplashScreen()
    }
}
