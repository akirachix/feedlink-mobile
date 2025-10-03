package com.feedlink.feedlink.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.feedlink.feedlink.screens.TimerScreen
import com.feedlink.feedlink.screens.WasteCollection
import com.feedlink.feedlink.screens.WasteHomepage
import com.feedlink.feedlink.screens.WasteHistory
import com.feedlink.feedlink.screens.WasteNotifications
import org.koin.androidx.compose.koinViewModel

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Collection : BottomNavItem("collection", "Collection", Icons.Default.ShoppingCart)
    object History : BottomNavItem("history", "History", Icons.Default.List)
    object Notifications :
        BottomNavItem("notifications", "Notifications", Icons.Default.Notifications)
}

@Composable
fun FeedLinkNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CurvedBottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                WasteHomepage()
            }
            composable(BottomNavItem.Collection.route) {
                WasteCollection(
                    viewModel = koinViewModel(),
                    onTimerClick = { claimId ->
                        navController.navigate("timer/$claimId")
                    },
                    onContinueClaimingClick = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(BottomNavItem.History.route) {
                WasteHistory(viewModel = koinViewModel())
            }
            composable(BottomNavItem.Notifications.route) {
                WasteNotifications()
            }
            composable(
                route = "timer/{claimId}",
                arguments = listOf(navArgument("claimId") { type = NavType.IntType })
            ) { backStackEntry ->
                val claimId = backStackEntry.arguments?.getInt("claimId")
                TimerScreen(onBackClick = { navController.popBackStack() }, claimId = claimId)
            }
        }
    }
}

@Composable
fun CurvedBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(top = 8.dp)
    ) {
        BottomAppBar(
            containerColor = Color.Transparent,
            contentColor = Color.Gray,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                BottomNavItem.Home,
                BottomNavItem.Collection,
                BottomNavItem.History,
                BottomNavItem.Notifications
            )

            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color(0xFF4CAF50) else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = if (isSelected) Color(0xFF4CAF50) else Color.Gray
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}