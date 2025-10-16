package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.network.Order
import com.feedlink.feedlink.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class SuccessSingle(val order: Order) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()


    fun fetchOrdersForCurrentUser() {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            val userIdString = tokenManager.getUserId()

            if (userIdString == null) {
                _uiState.value = OrderUiState.Error("User is not logged in.")
                return@launch
            }

            try {
                val allOrders = orderRepository.getAllOrders()

                val userSpecificOrders = allOrders.filter { order ->
                    order.user.toString() == userIdString
                }
                _uiState.value = OrderUiState.Success(userSpecificOrders)

            } catch (e: Exception) {
                Log.e("OrderViewModel", "Failed to fetch orders", e)
                _uiState.value = OrderUiState.Error("Failed to fetch order history: ${e.message}")
            }
        }
    }
    fun fetchOrderDetails(orderId: Int) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val response = orderRepository.getOrderById(orderId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = OrderUiState.SuccessSingle(response.body()!!)
                } else {
                    _uiState.value = OrderUiState.Error("Failed to load order details.")
                }
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error("Network error: ${e.message}")
            }
        }
    }
}
