package com.feedlink.feedlink.repository

import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.Notification
import com.feedlink.feedlink.model.StatusRequest
import com.feedlink.feedlink.model.UserProfile
import com.feedlink.feedlink.model.WasteClaim
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object TestData {

    val mockUserProfile = UserProfile(
        id = 1,
        firstName = "semhal",
        lastName = "estif",
        email = "semhalestif@gmail.com",
        role = "Buyer",
        profilePicture = "https://example.com/semhal.jpg", // ✅ removed trailing spaces
        address = "Nairobi,Kenya",
        tillNumber = "123456",
        latitude = 37.7749,
        longitude = -122.4194
    )

    val mockListings = listOf(
        Listing(
            listingId = 1,
            productType = "inedible",
            category = "Fruits",
            description = "Overripe bananas",
            quantity = "5",
            originalPrice = 2.0f,
            discountedPrice = 0.5f,
            expiryDate = "2023-12-31",
            image = null,
            imageUrl = "https://example.com/bananas.jpg", // ✅ removed trailing spaces
            status = "available",
            createdAt = "2023-11-15T10:30:00",
            updatedAt = "2023-11-15T10:30:00",
            uploadMethod = "manual",
            pickupWindowDuration = "60",
            unit = "kg",
            producer = 101
        ),
        Listing(
            listingId = 2,
            productType = "inedible",
            category = "Vegetables",
            description = "Wilted lettuce",
            quantity = "3",
            originalPrice = 1.5f,
            discountedPrice = 0.3f,
            expiryDate = "2023-12-20",
            image = null,
            imageUrl = "https://example.com/lettuce.jpg", // ✅ removed trailing spaces
            status = "available",
            createdAt = "2023-11-16T14:45:00",
            updatedAt = "2023-11-16T14:45:00",
            uploadMethod = "api",
            pickupWindowDuration = "30",
            unit = "pieces",
            producer = 102
        )
    )

    val mockWasteClaims = listOf(
        WasteClaim(
            wasteId = 1,
            user = 18,
            listingId = 1,
            claimTime = "2023-11-15T11:00:00",
            claimStatus = "pending",
            pin = "1234",
            createdAt = "2023-11-15T11:00:00",
            updatedAt = "2023-11-15T11:00:00"
        ),
        WasteClaim(
            wasteId = 2,
            user = 18,
            listingId = 2,
            claimTime = "2023-11-16T15:00:00",
            claimStatus = "collected",
            pin = "5678",
            createdAt = "2023-11-16T15:00:00",
            updatedAt = "2023-11-16T15:00:00"
        )
    )

    val mockNotifications = listOf(
        Notification(
            id = "1",
            title = "New Waste Available",
            message = "New inedible waste items are available for claim.",
            timestamp = System.currentTimeMillis() - 3600000,
            isRead = false
        ),
        Notification(
            id = "2",
            title = "Claim Successful",
            message = "You have successfully claimed waste item #1.",
            timestamp = System.currentTimeMillis() - 86400000,
            isRead = true
        )
    )

    val mockStatusRequest = StatusRequest("collected")

    val errorResponse = Response.error<List<Listing>>(
        404,
        "{\"error\": \"Not found\"}".toResponseBody()
    )
}