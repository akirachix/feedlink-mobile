package com.feedlink.feedlink.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date


class TimerViewModel(
    private val repository: WasteClaimRepository,
    private val claimId: Int
) : ViewModel() {
    private val _wasteClaim = MutableStateFlow<Result<WasteClaim>?>(null)
    val wasteClaim: StateFlow<Result<WasteClaim>?> = _wasteClaim.asStateFlow()


    private val _listing = MutableStateFlow<Result<Listing>?>(null)
    val listing: StateFlow<Result<Listing>?> = _listing.asStateFlow()


    private val _timerExpired = MutableStateFlow(false)
    val timerExpired: StateFlow<Boolean> = _timerExpired.asStateFlow()


    private val _isOverdue = MutableStateFlow(false)
    val isOverdue: StateFlow<Boolean> = _isOverdue.asStateFlow()


    private val _pickupDeadline = MutableStateFlow<Date?>(null)
    val pickupDeadline: StateFlow<Date?> = _pickupDeadline.asStateFlow()


    private val _totalTimeInSeconds = MutableStateFlow(3600)
    val totalTimeInSeconds: StateFlow<Int> = _totalTimeInSeconds.asStateFlow()


    init {
        fetchWasteClaimAndListing()
    }


    private fun fetchWasteClaimAndListing() {
        viewModelScope.launch {
            val claimResult = repository.getWasteClaimById(claimId)
            _wasteClaim.value = claimResult


            if (claimResult.isSuccess) {
                val claim = claimResult.getOrNull()
                val listingId = claim?.listingId
                if (listingId != null) {
                    val listingResult = repository.getListingById(listingId)
                    _listing.value = listingResult


                    if (listingResult.isSuccess) {
                        val listing = listingResult.getOrNull()
                        if (listing != null) {
                            calculatePickupDeadline(claim, listing)
                            checkIfOverdue()
                        }
                    }
                }
            }
        }
    }


    fun refresh() {
        fetchWasteClaimAndListing()
    }


    private fun calculatePickupDeadline(claim: WasteClaim, listing: Listing) {
        val deadlineStr = listing.pickupWindowDuration
        if (deadlineStr.isNullOrBlank()) {
            _pickupDeadline.value = null
            return
        }


        try {
            val deadlineDate = DateUtils.parseClaimTime(deadlineStr)
            val claimDate = claim.claimTime?.let { DateUtils.parseClaimTime(it) } ?: Date()
            _pickupDeadline.value = deadlineDate
            _totalTimeInSeconds.value = ((deadlineDate.time - claimDate.time) / 1000).toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            Log.e("TimerViewModel", "Error parsing pickup deadline", e)
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
            val currentClaim = _wasteClaim.value?.getOrNull()


            if (currentClaim?.wasteId != null) {
                val wasteId = currentClaim.wasteId
                Log.d("TimerViewModel", "Attempting to update status for wasteId: $wasteId")


                val result = repository.updateClaimStatus(wasteId, "expired")
                result.fold(
                    onSuccess = {
                        Log.d("TimerViewModel", "Successfully updated status to expired for wasteId: $wasteId")
                        val updatedClaimResult = repository.getWasteClaimById(claimId)
                        _wasteClaim.value = updatedClaimResult
                        _timerExpired.value = true
                    },
                    onFailure = { exception ->
                        Log.e("TimerViewModel", "Failed to update claim status on expiry for wasteId: $wasteId", exception)
                    }
                )
            } else {
                Log.e("TimerViewModel", "Cannot update status: claim or wasteId is null.")
            }
        }
    }
}

