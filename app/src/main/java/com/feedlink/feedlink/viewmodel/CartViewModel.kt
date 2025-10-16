package com.feedlink.feedlink.viewmodel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.model.ListingItem
import com.feedlink.feedlink.network.OrderCreationRequest
import com.feedlink.feedlink.repository.CartRepository
import com.feedlink.feedlink.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    data class NavigateToPayment(val orderId: Int, val amount: Int) : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

class CartViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    val cartItems: SnapshotStateList<ListingItem> = CartRepository.cartItems

    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val checkoutState = _checkoutState.asStateFlow()

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

    fun initiateCheckout() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutUiState.Loading
            val userId = TokenManager.getUserId()

            if (userId == null) {
                _checkoutState.value = CheckoutUiState.Error("You are not logged in.")
                return@launch
            }
            if (cartItems.isEmpty()) {
                _checkoutState.value = CheckoutUiState.Error("Your cart is empty.")
                return@launch
            }

            try {
                val orderRequest = OrderCreationRequest(
                    userId = userId.toInt(),
                    orderStatus = "pending",
                    paymentStatus = "unpaid"
                )

                val response = orderRepository.createOrder(orderRequest)
                val newOrder = response.body()

                if (response.isSuccessful && newOrder != null) {
                    _checkoutState.value = CheckoutUiState.NavigateToPayment(
                        orderId = newOrder.orderId,
                        amount = totalPrice
                    )
                    cartItems.clear()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to create order."
                    _checkoutState.value = CheckoutUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _checkoutState.value = CheckoutUiState.Error("A network error occurred: ${e.message}")
            }
        }
    }

    fun checkoutStateConsumed() {
        _checkoutState.value = CheckoutUiState.Idle
    }
}

