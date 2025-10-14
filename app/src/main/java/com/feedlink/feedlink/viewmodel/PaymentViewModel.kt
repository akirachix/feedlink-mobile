package com.feedlink.feedlink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.network.PaymentStatusResponse
import com.feedlink.feedlink.network.StkPushRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    object Polling : PaymentUiState()
    data class StkPushSuccess(val paymentId: String, val orderId: Int) : PaymentUiState()
    data class OrderConfirmed(val details: PaymentStatusResponse, val orderId: Int) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel(private val apiInterface: ApiInterface) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentState: StateFlow<PaymentUiState> = _paymentState

    fun initiateStkPush(phoneNumber: String, amount: Int, orderId: Int) {
        viewModelScope.launch {
            _paymentState.value = PaymentUiState.Loading
            try {
                val request = StkPushRequest(
                    phoneNumber = phoneNumber,
                    amount = amount,
                    accountReference = orderId.toString(),
                    transactionDesc = "Payment for Feedlink Order #$orderId"
                )

                val response = apiInterface.initiateStkPush(request)
                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {
                    _paymentState.value = PaymentUiState.StkPushSuccess(responseBody.paymentId, orderId)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to initiate M-Pesa payment. Please try again."
                    _paymentState.value = PaymentUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentUiState.Error("A network error occurred: ${e.message}")
            }
        }
    }

    fun startPaymentPolling(paymentId: String, orderId: Int) {
        viewModelScope.launch {
            _paymentState.value = PaymentUiState.Polling
            val maxAttempts = 24
            var attempts = 0
            while (attempts < maxAttempts) {
                try {
                    val statusResponse = apiInterface.getPaymentStatus(paymentId)

                    if (statusResponse.isSuccessful && statusResponse.body() != null) {
                        val paymentDetails = statusResponse.body()!!
                        if (paymentDetails.status.equals("confirmed", ignoreCase = true)) {
                            _paymentState.value = PaymentUiState.OrderConfirmed(paymentDetails, orderId)
                            return@launch
                        } else if (paymentDetails.status.equals("failed", ignoreCase = true)) {
                            _paymentState.value = PaymentUiState.Error("M-Pesa payment failed or was cancelled.")
                            return@launch
                        }
                    }
                } catch (e: Exception) {
                    println("Polling failed for paymentId $paymentId, will retry: ${e.message}")
                }
                attempts++
                delay(5000)
            }
            _paymentState.value = PaymentUiState.Error("Payment timed out. Please check your M-Pesa messages or try again.")
        }
    }

    fun resetState() {
        _paymentState.value = PaymentUiState.Idle
    }
}