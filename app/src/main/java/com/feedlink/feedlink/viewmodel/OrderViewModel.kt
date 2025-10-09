package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.network.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

class OrderViewModel(private val apiInterface: ApiInterface) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        fetchAllOrders()
    }

    fun fetchAllOrders() {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val fetchedOrders = apiInterface.getOrders()

                val sortedOrders = fetchedOrders.sortedByDescending { it.orderDate }

                _uiState.value = OrderUiState.Success(sortedOrders)

            } catch (e: Exception) {
                Log.e("OrderViewModel", "Failed to fetch all orders", e)
                _uiState.value = OrderUiState.Error("Could not load your order history. Please try again.")
            }
        }
    }
}
