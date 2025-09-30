package com.feedlink.feedlink.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feedlink.screens.WelcomeScreen
import kotlinx.coroutines.delay


sealed class Screen(val routeDefinition: String) {
    object Splash : Screen("splash")
    object Teaser1 : Screen("teaser1")
    object Teaser2 : Screen("teaser2")
    object Teaser3 : Screen("teaser3")
    object AuthChoice : Screen("auth_choice")
    object RoleChoice : Screen("role_choice")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up/{userRole}") {
        fun createRoute(roleValue: String) = "sign_up/$roleValue"
    }

    object ForgotPassword : Screen("forgot_password")
    object Verification : Screen("verification/{email}") {
        fun createRoute(emailValue: String) = "verification/$emailValue"
    }

    object ResetPassword : Screen("reset_password/{email}/{otp}") {
        fun createRoute(emailValue: String, otpValue: String) =
            "reset_password/$emailValue/$otpValue"
    }

    val route: String get() = routeDefinition.substringBefore("/{")
}

@Composable
fun FeedLinkNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.routeDefinition,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.routeDefinition) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(2000)
                navController.navigate(Screen.Teaser1.routeDefinition) {
                    popUpTo(Screen.Splash.routeDefinition) { inclusive = true }
                }
            }
        }

        composable(Screen.Teaser1.routeDefinition) {
            WelcomeScreen(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.routeDefinition) },
                onNextClicked = { navController.navigate(Screen.Teaser2.routeDefinition) }
            )
        }

        composable(Screen.Teaser2.routeDefinition) {
            SecondWelcome(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.routeDefinition) },
                onNextClicked = { navController.navigate(Screen.Teaser3.routeDefinition) }
            )
        }

        composable(Screen.Teaser3.routeDefinition) {
            ThirdWelcome(
                onSkipClicked = { navController.navigate(Screen.AuthChoice.routeDefinition) },
                onNextClicked = { navController.navigate(Screen.AuthChoice.routeDefinition) }
            )
        }


        composable(Screen.RoleChoice.routeDefinition) {
            WelcomeRoleScreen(
                onCustomerClick = {
                    navController.navigate(Screen.SignUp.createRoute("buyer"))
                },
                onRecyclerClick = {
                    navController.navigate(Screen.SignUp.createRoute("recycler"))
                }
            )
        }

        composable(Screen.AuthChoice.routeDefinition) {
            AuthChoiceScreen(
                onSignUpClick = { navController.navigate(Screen.RoleChoice.routeDefinition) },
                onSignInClick = { navController.navigate(Screen.SignIn.routeDefinition) }
            )
        }


        composable(Screen.SignIn.routeDefinition) {
            SignInScreen(
                onSignInSuccess = {

                },
                onSignUpClick = { navController.navigate(Screen.RoleChoice.routeDefinition) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.routeDefinition) }
            )
        }

        composable(
            route = Screen.SignUp.routeDefinition,
            arguments = listOf(navArgument("userRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val passedRole = backStackEntry.arguments?.getString("userRole")
            if (passedRole != null) {
                SignUpScreen(
                    userRole = passedRole,
                    onSignUpSuccess = {
                        navController.navigate(Screen.SignIn.routeDefinition) {
                            popUpTo(Screen.AuthChoice.routeDefinition) { inclusive = true }
                        }
                    },
                    onSignInClick = { navController.navigate(Screen.SignIn.routeDefinition) }
                )
            } else {
                Text("Error: User role not provided to SignUp screen.")
            }
        }

        composable(Screen.ForgotPassword.routeDefinition) {
            ForgotPasswordScreen { email ->
                navController.navigate(Screen.Verification.createRoute(email))
            }
        }

        composable(
            route = Screen.Verification.routeDefinition,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCodeScreen(
                email = email,
                onVerificationSuccess = { otp ->
                    navController.navigate(Screen.ResetPassword.createRoute(email, otp))
                },
                onResendClick = {
                }
            )
        }

        composable(
            route = Screen.ResetPassword.routeDefinition,
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
                    navController.navigate(Screen.SignIn.routeDefinition) {
                        popUpTo(Screen.Splash.routeDefinition) { inclusive = true }
                    }
                }
            )
        }
    }
}
