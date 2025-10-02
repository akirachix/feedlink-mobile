package com.feedlink.feedlink.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.WasteClaimRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val claimDate = dateFormat.parse(claimTime)

                val claimCalendar = Calendar.getInstance()
                claimCalendar.time = claimDate

                val pickupCalendar = Calendar.getInstance()
                pickupCalendar.time = claimDate
                pickupCalendar.set(Calendar.HOUR_OF_DAY, 9)
                pickupCalendar.set(Calendar.MINUTE, 0)
                pickupCalendar.set(Calendar.SECOND, 0)
                pickupCalendar.set(Calendar.MILLISECOND, 0)


                if (pickupCalendar.before(claimCalendar)) {
                    pickupCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                _pickupDeadline.value = pickupCalendar.time
            } catch (e: Exception) {
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
        val deadline = _pickupDeadline.value
        if (deadline != null) {
            val now = Date()
            val timeLeftInMillis = deadline.time - now.time
            return if (timeLeftInMillis > 0) (timeLeftInMillis / 1000).toInt() else 0
        }
        return 0
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