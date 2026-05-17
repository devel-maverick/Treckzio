package com.weathersnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.weathersnap.app.ui.navigation.AppNavGraph
import com.weathersnap.app.ui.theme.Background
import com.weathersnap.app.ui.theme.WeatherSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WeatherSnapTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .windowInsetsPadding(WindowInsets.systemBars),
                    color = Background
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
