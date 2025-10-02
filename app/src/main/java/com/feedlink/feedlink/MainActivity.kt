package com.feedlink.feedlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.feedlink.feedlink.screens.FeedLinkNavHost
import com.feedlink.feedlink.ui.theme.FeedlinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedlinkTheme {
                val navController = rememberNavController()
                FeedLinkNavHost(navController = navController)
            }
        }
    }
}