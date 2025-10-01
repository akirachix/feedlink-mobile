
package com.feedlink.feedlink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.feedlink.feedlink.screens.ListingScreen
import com.feedlink.feedlink.screens.ProductDetailScreen
import com.feedlink.feedlink.screens.CartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            ListingScreen(
                onNavigateToProductDetail = { listingId ->
                    navController.navigate("product_detail/$listingId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToOrders = {
                    navController.navigate("orders")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                }
            )
        }

        composable(
            route = "product_detail/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: -1
            ProductDetailScreen(
                listingId = listingId,
                onBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToCart = {
                    navController.navigate("cart") {
                        launchSingleTop = true
                    }
                },
                onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }

        composable("cart") {
            CartScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onProceedToCheckout = {

                }
            )
        }

        composable("orders") {
        }

        composable("notifications") {
        }

        composable("profile") {
        }
    }
}
