package com.feedlink.feedlink.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.feedlink.feedlink.auth.TokenManager

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "view_profile_screen"
    ) {
        composable(route = "view_profile_screen") {
            ViewProfileScreen(
                onNavigateToEdit = { userId ->
                    navController.navigate("edit_profile/$userId")
                },
                onLogout = {
                    TokenManager.clearAuthData()
                    navController.navigate("login_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = "edit_profile/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = androidx.navigation.NavType.IntType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            if (userId != null) {
                EditProfileFormScreen(
                    userIdToEdit = userId,
                    onProfileUpdated = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                androidx.compose.material3.Text("User ID missing")
            }
        }
    }
}
