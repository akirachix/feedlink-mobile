package com.feedlink.feedlink

import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.Notification
import com.feedlink.feedlink.model.StatusRequest
import com.feedlink.feedlink.model.WasteClaim
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object TestData {

    val mockListings = listOf(
        Listing(
            listingId = 1,
            productType = "inedible",
            category = "Fruits",
            description = "Overripe bananas",
            quantity = "5",
            originalPrice = "$2.00",
            expiryDate = "2023-12-31",
            discountedPrice = "$0.50",
            imageUrl = "https://example.com/bananas.jpg",
            status = "available",
            createdAt = "2023-11-15T10:30:00",
            updatedAt = "2023-11-15T10:30:00",
            unit = "kg"
        ),
        Listing(
            listingId = 2,
            productType = "inedible",
            category = "Vegetables",
            description = "Wilted lettuce",
            quantity = "3",
            originalPrice = "$1.50",
            expiryDate = "2023-12-20",
            discountedPrice = "$0.30",
            imageUrl = "https://example.com/lettuce.jpg",
            status = "available",
            createdAt = "2023-11-16T14:45:00",
            updatedAt = "2023-11-16T14:45:00",
            unit = "pieces"
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