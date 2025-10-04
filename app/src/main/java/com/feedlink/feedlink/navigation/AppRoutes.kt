package com.feedlink.feedlink.navigation
sealed class AppScreen(val route: String) {
    object ViewProfile : AppScreen("view_profile_screen")
    object EditProfileForm : AppScreen("edit_profile_form_screen/{userId}") {
        fun createRoute(userId: Int) = "edit_profile_form_screen/$userId"
    }
}

