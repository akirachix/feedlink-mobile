package com.feedlink.feedlink.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.feedlink.feedlink.model.ListingItem
import com.feedlink.feedlink.repository.CartRepository

class CartViewModel : ViewModel() {

    val cartItems: SnapshotStateList<ListingItem> = CartRepository.cartItems

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val totalItems: Int
        get() = cartItems.sumOf { it.quantity?.toIntOrNull() ?: 0 }

    val totalPrice: Int
        get() = cartItems.sumOf {
            val price = it.discountedPrice ?: it.originalPrice ?: 0f
            val quantity = it.quantity?.toIntOrNull() ?: 1
            (price * quantity).toInt()
        }

    fun onQuantityChange(itemId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            onRemoveItem(itemId)
            return
        }
        val itemIndex = cartItems.indexOfFirst { it.listingId == itemId }
        if (itemIndex != -1) {
            val updatedItem = cartItems[itemIndex].copy(quantity = newQuantity.toString())
            cartItems[itemIndex] = updatedItem
        }
    }

    fun onRemoveItem(itemId: Int) {
        cartItems.removeAll { it.listingId == itemId }
    }

    fun initiateCheckout(phoneNumber: String) {
        println("Initiating checkout for $phoneNumber with ${cartItems.size} items")
    }
}