
package com.feedlink.feedlink.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feedlink.screens.WelcomeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Teaser1 : Screen("teaser1")
    object Teaser2 : Screen("teaser2")
    object Teaser3 : Screen("teaser3")
    object AuthChoice : Screen("auth_choice")
    object RoleChoice : Screen("role_choice")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up/{userRole}") {
        fun createRoute(role: String) = "sign_up/$role"
    }
    object ForgotPassword : Screen("forgot_password")
    object Verification : Screen("verification/{email}") {
        fun createRoute(email: String) = "verification/$email"
    }
    object ResetPassword : Screen("reset_password/{email}/{otp}") {
        fun createRoute(email: String, otp: String) = "reset_password/$email/$otp"
    }

    object Home : Screen("home")
    object ProductDetail : Screen("product_detail/{listingId}") {
        fun createRoute(id: Int) = "product_detail/$id"
    }
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
}

@Composable
fun FeedLinkNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen()
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                navController.navigate(Screen.Teaser1.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Teaser1.route) {
            WelcomeScreen(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.route) },
                onNextClicked = { navController.navigate(Screen.Teaser2.route) }
            )
        }

        composable(Screen.Teaser2.route) {
            SecondWelcome(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.route) },
                onNextClicked = { navController.navigate(Screen.Teaser3.route) }
            )
        }

        composable(Screen.Teaser3.route) {
            ThirdWelcome(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.route) },
                onNextClicked = { navController.navigate(Screen.AuthChoice.route) }
            )
        }

        composable(Screen.AuthChoice.route) {
            AuthChoiceScreen(
                onSignUpClick = { navController.navigate(Screen.RoleChoice.route) },
                onSignInClick = { navController.navigate(Screen.SignIn.route) }
            )
        }

        composable(Screen.RoleChoice.route) {
            WelcomeRoleScreen(
                onCustomerClick = { navController.navigate(Screen.SignUp.createRoute("buyer")) },
                onRecyclerClick = { navController.navigate(Screen.SignUp.createRoute("recycler")) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Screen.RoleChoice.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(
            route = Screen.SignUp.route,
            arguments = listOf(navArgument("userRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val passedRole = backStackEntry.arguments?.getString("userRole") ?: "buyer"
            SignUpScreen(
                userRole = passedRole,
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignInClick = { navController.navigate(Screen.SignIn.route) }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen { email ->
                navController.navigate(Screen.Verification.createRoute(email))
            }
        }

        composable(
            route = Screen.Verification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCodeScreen(
                email = email,
                onVerificationSuccess = { otp ->
                    navController.navigate(Screen.ResetPassword.createRoute(email, otp))
                },
                onResendClick = { }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val otp = backStackEntry.arguments?.getString("otp") ?: ""
            ResetPasswordScreen(
                email = email,
                otp = otp,
                onResetSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            ListingScreen(
                onNavigateToProductDetail = { id ->
                    navController.navigate(Screen.ProductDetail.createRoute(id))
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: -1
            ProductDetailScreen(
                listingId = listingId,
                onBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onProceedToCheckout = {}
            )
        }

        composable(Screen.Orders.route) {
        }

        composable(Screen.Notifications.route) {
        }

        composable(Screen.Profile.route) {
        }
    }
}