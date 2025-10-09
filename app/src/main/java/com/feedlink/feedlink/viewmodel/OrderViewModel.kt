package com.feedlink.feedlink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.network.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

class OrderViewModel(private val apiInterface: ApiInterface) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState

    init {
        fetchAllOrders()
    }

    fun fetchAllOrders() {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {

                val mockOrders = listOf(
                    Order(orderId = 98, orderDate = "2025-10-08T13:02:33.358961Z", orderStatus = "pending", pin = "8888"),
                    Order(orderId = 97, orderDate = "2025-10-08T07:01:15.771760Z", orderStatus = "picked", pin = "7487"),
                    Order(orderId = 47, orderDate = "2025-10-06T06:04:45.879402Z", orderStatus = "pending", pin = "6666")
                )
                _uiState.value = OrderUiState.Success(mockOrders)

            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error("Failed to connect to the server.")
            }
        }
    }
}
