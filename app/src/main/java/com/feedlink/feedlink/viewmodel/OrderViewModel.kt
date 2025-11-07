package com.feedlink.feedlink.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.network.Order
import com.feedlink.feedlink.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeParseException

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class SuccessSingle(val order: Order) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

@RequiresApi(Build.VERSION_CODES.O)
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        fetchOrdersForCurrentUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                Log.d("OrderViewModel", "Total orders from backend: ${allOrders.size}")

                val userSpecificOrders = allOrders.filter { order ->
                    order.user.toString() == userIdString
                }

                val sortedOrders = userSpecificOrders.sortedByDescending { order ->
                    try {
                        Instant.parse(order.orderDate)
                    } catch (e: DateTimeParseException) {
                        Log.w("OrderViewModel", "Failed to parse date for order ${order.orderId}: ${order.orderDate}", e)
                        Instant.MIN
                    }
                }

                Log.d("OrderViewModel", "Fetched and sorted ${sortedOrders.size} orders for user $userIdString")
                _uiState.value = OrderUiState.Success(sortedOrders)

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

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val currentOrders = (_uiState.value as? OrderUiState.Success)?.orders ?: emptyList()
                val updatedOrders = currentOrders.map { order ->
                    if (order.orderId == orderId) {
                        Log.d("OrderViewModel", "Updating order $orderId status to '$newStatus' locally")
                        order.copy(orderStatus = newStatus)
                    } else {
                        order
                    }
                }
                _uiState.value = OrderUiState.Success(updatedOrders)

                Log.d("OrderViewModel", "Attempting to update order $orderId on backend. Setting order_status to '$newStatus'")
                val response = orderRepository.updateOrderStatus(orderId, newStatus)

                if (response.isSuccessful) {
                    Log.d("OrderViewModel", "Successfully updated order_status on backend. Response code: ${response.code()}")
                    fetchOrdersForCurrentUser()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OrderViewModel", "Failed to update order_status. Code: ${response.code()}, Error: $errorBody")
                    _uiState.value = OrderUiState.Error("Failed to update status: ${response.code()}. Please try again.")
                    fetchOrdersForCurrentUser()
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Exception during order status update", e)
                _uiState.value = OrderUiState.Error("Network error: ${e.message}")
                fetchOrdersForCurrentUser()
            }
        }
    }
}