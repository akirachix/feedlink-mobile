package com.feedlink.feedlink.repository

import com.feedlink.feedlink.model.UserProfile

object TestData {

    val mockUserProfile = UserProfile(
        id = 1,
        firstName = "Jane",
        lastName = "Doe",
        email = "jane@example.com",
        role = "User",
        profilePicture = "https://example.com/profile.jpg",
        address = "123 Main St",
        tillNumber = "123456",
        latitude = 37.7749,
        longitude = -122.4194
    )
}
