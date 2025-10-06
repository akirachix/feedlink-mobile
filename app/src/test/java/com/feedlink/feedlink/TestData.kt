package com.feedlink.feedlink.repository

import com.feedlink.feedlink.model.UserProfile

object TestData {

    val mockUserProfile = UserProfile(
        id = 1,
        firstName = "semhal",
        lastName = "estif",
        email = "semhalestif@gmail.com",
        role = "Buyer",
        profilePicture = "https://example.com/semhal.jpg",
        address = "Nairobi,Kenya",
        tillNumber = "123456",
        latitude = 37.7749,
        longitude = -122.4194
    )
}
