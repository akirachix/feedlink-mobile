package com.feedlink.feedlink

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.feedlink.feedlink.screens.AppNavigation
import androidx.navigation.compose.rememberNavController
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.screens.FeedLinkNavHost
import com.feedlink.feedlink.screens.Screen
import com.feedlink.feedlink.ui.theme.FeedlinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.initialize(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                    FeedlinkTheme {
                        val prefs = getSharedPreferences("FEEDLINK_PREFS", Context.MODE_PRIVATE)
                        val token = prefs.getString("ACCESS_TOKEN", null)
                        val role = prefs.getString("USER_ROLE", null)

                        val startDestination = when {
                            !token.isNullOrBlank() && !role.isNullOrBlank() -> {
                                when (role) {
                                    "buyer" -> Screen.Home.route
                                    "recycler" -> Screen.RecyclerHome.route
                                    else -> Screen.Splash.route
                                }
                            }

                            else -> Screen.Splash.route
                        }

                        val navController = rememberNavController()
                        FeedLinkNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }}


