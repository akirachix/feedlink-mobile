package com.feedlink.feedlink.screens
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feedlink.screens.WelcomeScreen
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.viewmodel.CartViewModel
import com.feedlink.feedlink.viewmodel.TimerViewModel
import com.feedlink.feedlink.viewmodel.WasteClaimViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf




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
    object BuyerNotifications : Screen("buyer_notifications")
    object Profile : Screen("profile")
    object RecyclerHome : Screen("recycler_home")
    object Collection : Screen("collection")
    object History : Screen("history")
    object RecyclerNotifications : Screen("recycler_notifications")
    object Timer : Screen("timer/{claimId}") {
        fun createRoute(claimId: Int) = "timer/$claimId"
    }

    object PaymentMethod : Screen("payment_method/{orderId}/{totalAmount}") {
        fun createRoute(orderId: Int, totalAmount: Int) = "payment_method/$orderId/$totalAmount"
    }
    object OrderConfirmed : Screen("order_confirmed_screen/{orderId}") {
        fun createRoute(orderId: Int) = "order_confirmed_screen/$orderId"
    }
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

        composable(
            route = Screen.PaymentMethod.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType },
                navArgument("totalAmount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            val totalAmount = backStackEntry.arguments?.getInt("totalAmount") ?: 0
            PaymentMethodScreen(
                navController = navController,
                orderId = orderId,
                totalAmount = totalAmount
            )
        }

        composable(Screen.OrderConfirmed.route) {
            OrderConfirmedScreen(navController = navController)
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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRecyclerHome = {
                    navController.navigate(Screen.RecyclerHome.route) {
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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRecyclerHome = {
                    navController.navigate(Screen.RecyclerHome.route) {
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
                    navController.navigate(Screen.SignIn.route) {
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
                onNavigateToProfile = { navController.navigate("view_profile_screen") },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToNotifications = { navController.navigate(Screen.BuyerNotifications.route) }
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
                onNavigateToNotifications = { navController.navigate(Screen.BuyerNotifications.route) }
            )
        }




        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel()
            val totalPrice = cartViewModel.totalPrice
            CartScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToNotifications = { navController.navigate(Screen.BuyerNotifications.route) },
                onProceedToCheckout = {
                    val orderId = 48
                    val totalAmount = totalPrice
                    navController.navigate(Screen.PaymentMethod.createRoute(orderId, totalAmount))
                }
            )
        }



        composable(Screen.Orders.route) {
            OrderHistory(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToOrders = {
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.BuyerNotifications.route) {
                        launchSingleTop = true
                    }
                }
            )
        }



        composable(Screen.BuyerNotifications.route) {
            BuyerNotifications(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToNotifications = {
                }
            )
        }







        composable(route = "view_profile_screen") {
            ViewProfileScreen(
                onNavigateToEdit = { userId ->
                    navController.navigate("edit_profile/$userId")
                },
                onLogout = {
                    TokenManager.clearAuthData()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }




        composable(
            route = "edit_profile/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
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




        composable(Screen.RecyclerHome.route) {
            RecyclerAppNavGraph(navController = navController)
        }
        composable(Screen.Collection.route) {
            RecyclerAppNavGraph(navController = navController)
        }
        composable(Screen.History.route) {
            RecyclerAppNavGraph(navController = navController)
        }
        composable(Screen.RecyclerNotifications.route) {
            RecyclerAppNavGraph(navController = navController)
        }
        composable(
            route = Screen.Timer.route,
            arguments = listOf(navArgument("claimId") { type = NavType.IntType })
        ) { backStackEntry ->
            val claimId = backStackEntry.arguments?.getInt("claimId") ?: -1
            if (claimId == -1) return@composable




            val viewModel: TimerViewModel = koinViewModel(
                key = "timer_vm_$claimId",
                parameters = { parametersOf(claimId) }
            )




            TimerScreen(
                onBackClick = { navController.popBackStack() },
                claimId = claimId
            )
        }
    }
}

@Composable
fun BuyerNotificationsScreen() {
    TODO("Not yet implemented")
}




@Composable
fun RecyclerAppNavGraph(navController: NavController) {
    val recyclerNavController = rememberNavController()
    val navBackStackEntry by recyclerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            CurvedBottomNavigationBar(
                navController = recyclerNavController,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = recyclerNavController,
            startDestination = Screen.RecyclerHome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.RecyclerHome.route) {
                RecyclerHome(
                    navToProfile = {
                        navController.navigate("view_profile_screen")
                    }
                )
            }
            composable(Screen.Collection.route) {
                WasteCollection(
                    viewModel = koinViewModel<WasteClaimViewModel>(),
                    onTimerClick = { claimId ->
                        navController.navigate(Screen.Timer.createRoute(claimId))
                    },
                    onContinueClaimingClick = {
                        recyclerNavController.navigate(Screen.RecyclerHome.route) {
                            popUpTo(Screen.RecyclerHome.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.History.route) {
                WasteHistory(viewModel = koinViewModel())
            }
            composable(Screen.RecyclerNotifications.route) {
                WasteNotifications()
            }
        }
    }
}
@Composable
fun CurvedBottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
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
                Screen.RecyclerHome to Icons.Default.Home,
                Screen.Collection to Icons.Default.ShoppingCart,
                Screen.History to Icons.Default.List,
                Screen.RecyclerNotifications to Icons.Default.Notifications
            )




            items.forEach { (screen, icon) ->
                val isSelected = currentRoute == screen.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.route,
                            tint = if (isSelected) Color(0xFFFF8614) else Color(0xFF234B06)
                        )
                    },
                    label = {
                        Text(
                            text = when (screen) {
                                Screen.RecyclerHome -> "Home"
                                Screen.Collection -> "Collection"
                                Screen.History -> "History"
                                Screen.RecyclerNotifications -> "Notifications"
                                else -> "Unknown"
                            },
                            color = if (isSelected) Color(0xFFFF8614) else Color(0xFF234B06)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}




