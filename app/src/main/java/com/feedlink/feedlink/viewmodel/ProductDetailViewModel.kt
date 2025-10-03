package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.ListingItem
import com.feedlink.feedlink.repository.CartRepository
import com.feedlink.feedlink.repository.ListingsRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ListingsRepository
) : ViewModel() {

    val product = mutableStateOf<Listing?>(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val quantity = mutableStateOf(1)

    fun fetchProductDetail(listingId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            try {
                Log.d("ProductDetailViewModel", "Fetching product details for ID: $listingId")
                val response = repository.fetchProductDetail(listingId)

                if (response.isSuccessful) {
                    response.body()?.let { productData ->
                        Log.d(
                            "ProductDetailViewModel",
                            "Successfully fetched product: ${productData.description}"
                        )
                        product.value = productData
                        quantity.value = 1
                    } ?: run {
                        Log.e(
                            "ProductDetailViewModel",
                            "Response body is null for product ID: $listingId"
                        )
                        error.value = "Product not found"
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(
                        "ProductDetailViewModel",
                        "Error response: ${response.code()} - $errorBody"
                    )
                    error.value = "Error: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e("ProductDetailViewModel", "Exception fetching product details", e)
                error.value = "Exception: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateQuantity(newQuantity: Int) {
        if (newQuantity > 0) {
            quantity.value = newQuantity
        }
    }

    fun addToCart() {
        product.value?.let { listing ->
            val newQuantity = quantity.value
            val existingIndex = CartRepository.cartItems.indexOfFirst { it.listingId == listing.listingId }

            if (existingIndex != -1) {
                val existingItem = CartRepository.cartItems[existingIndex]
                val currentQty = existingItem.quantity?.toIntOrNull() ?: 0
                val updatedQty = currentQty + newQuantity
                CartRepository.cartItems[existingIndex] = existingItem.copy(
                    quantity = updatedQty.toString()
                )
                Log.d("ProductDetailViewModel", "Updated item ${listing.listingId} quantity to $updatedQty")
            } else {

                val cartItem = ListingItem(
                    listingId = listing.listingId,
                    description = listing.description,
                    originalPrice = listing.originalPrice,
                    discountedPrice = listing.discountedPrice,
                    image = listing.imageUrl ?: listing.image,
                    quantity = newQuantity.toString()
                )
                CartRepository.cartItems.add(cartItem)
                Log.d("ProductDetailViewModel", "Added new item to cart: ${listing.description}")
            }
        }
    }

    fun resetState() {
        product.value = null
        isLoading.value = false
        error.value = null
        quantity.value = 1
    }
}