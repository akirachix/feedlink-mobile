// NotificationViewModel.kt
package com.feedlink.feedlink.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationUiState {
    object Loading : NotificationUiState()
    data class Success(val notifications: List<Notification>) : NotificationUiState()
    data class Error(val message: String) : NotificationUiState()
}

class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            try {
                _notifications.value = emptyList()
                _uiState.value = NotificationUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = NotificationUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            _notifications.value = _notifications.value + notification
            _uiState.value = NotificationUiState.Success(_notifications.value)
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            _notifications.value = _notifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _uiState.value = NotificationUiState.Success(_notifications.value)
        }
    }
}