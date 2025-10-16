package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.network.Order
import com.feedlink.feedlink.network.OrderCreationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class OrderRepository(private val apiInterface: ApiInterface) {
    suspend fun createOrder(orderRequest: OrderCreationRequest): Response<Order> {
        return withContext(Dispatchers.IO) {
            apiInterface.createOrder(orderRequest)
        }
    }

    suspend fun getOrderById(orderId: Int): Response<Order> {
        return withContext(Dispatchers.IO) {
            apiInterface.getOrderById(orderId)
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return withContext(Dispatchers.IO) {
            apiInterface.getAllOrders()
        }
    }
}
