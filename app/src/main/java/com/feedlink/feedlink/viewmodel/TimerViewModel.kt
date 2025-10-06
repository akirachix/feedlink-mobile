package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.WasteClaimRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimerViewModel(
    private val repository: WasteClaimRepository,
    private val claimId: Int
) : ViewModel() {
    private val _wasteClaim = MutableStateFlow<Result<WasteClaim>?>(null)
    val wasteClaim: StateFlow<Result<WasteClaim>?> = _wasteClaim.asStateFlow()

    private val _timerExpired = MutableStateFlow(false)
    val timerExpired: StateFlow<Boolean> = _timerExpired.asStateFlow()

    private val _isOverdue = MutableStateFlow(false)
    val isOverdue: StateFlow<Boolean> = _isOverdue.asStateFlow()

    private val _pickupDeadline = MutableStateFlow<Date?>(null)
    val pickupDeadline: StateFlow<Date?> = _pickupDeadline.asStateFlow()

    private val _totalTimeInSeconds = MutableStateFlow(3600)
    val totalTimeInSeconds: StateFlow<Int> = _totalTimeInSeconds.asStateFlow()

    init {
        fetchWasteClaim()
    }

    fun fetchWasteClaim() {
        viewModelScope.launch {
            _wasteClaim.value = repository.getWasteClaimById(claimId)
            val result = _wasteClaim.value
            if (result?.isSuccess == true) {
                val claim = result.getOrNull()
                if (claim != null) {
                    calculatePickupDeadline(claim)
                    checkIfOverdue()
                }
            }
        }
    }

    private fun calculatePickupDeadline(claim: WasteClaim) {
        val claimTime = claim.claimTime
        if (claimTime != null) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val claimDate = dateFormat.parse(claimTime)

                val claimCalendar = Calendar.getInstance().apply { time = claimDate!! }
                val pickupCalendar = Calendar.getInstance().apply {
                    time = claimDate!!
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                _pickupDeadline.value = pickupCalendar.time
                _totalTimeInSeconds.value = ((pickupCalendar.timeInMillis - claimCalendar.timeInMillis) / 1000).toInt()
            } catch (e: Exception) {
                Log.e("TimerViewModel", "Error parsing claim time", e)
                _pickupDeadline.value = null
            }
        } else {
            _pickupDeadline.value = null
        }
    }

    private fun checkIfOverdue() {
        val deadline = _pickupDeadline.value
        if (deadline != null) {
            val now = Date()
            _isOverdue.value = now.after(deadline)
            if (_isOverdue.value) {
                _timerExpired.value = true
            }
        } else {
            _isOverdue.value = false
        }
    }

    fun getTimeLeftInSeconds(): Int {
        val deadline = _pickupDeadline.value ?: return 0
        val now = Date()
        val timeLeftInMillis = deadline.time - now.time
        return if (timeLeftInMillis > 0) (timeLeftInMillis / 1000).toInt() else 0
    }

    fun onTimerExpired() {
        viewModelScope.launch {
            val result = repository.updateClaimStatus(claimId, "claim")
            if (result.isSuccess) {
                _wasteClaim.value = result
                _timerExpired.value = true
            }
        }
    }
}