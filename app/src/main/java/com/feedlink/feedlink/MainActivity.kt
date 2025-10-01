package com.feedlink.feedlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.feedlink.feedlink.navigation.AppNavigation
import com.feedlink.feedlink.ui.theme.FeedlinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedlinkTheme {
                AppNavigation()
            }
        }
    }
}