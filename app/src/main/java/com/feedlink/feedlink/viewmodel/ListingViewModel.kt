package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class ListingUiState {
    object Loading : ListingUiState()
    data class Success(val listings: List<Listing>) : ListingUiState()
    data class Error(val message: String) : ListingUiState()
}

class ListingViewModel(
    private val repository: ListingRepository,
    private val wasteClaimRepository: WasteClaimRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListingUiState>(ListingUiState.Loading)
    val uiState: StateFlow<ListingUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showClaimSuccessDialog = MutableStateFlow(false)
    val showClaimSuccessDialog: StateFlow<Boolean> = _showClaimSuccessDialog.asStateFlow()

    private val _claimedListings = MutableStateFlow<Set<Int>>(emptySet())
    val claimedListings: StateFlow<Set<Int>> = _claimedListings.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _previousListingCount = MutableStateFlow(0)
    private val _newListingDetected = MutableStateFlow(false)
    val newListingDetected: StateFlow<Boolean> = _newListingDetected

    private var lastKnownLatitude: Double? = null
    private var lastKnownLongitude: Double? = null

    fun fetchInedibleListings(latitude: Double?, longitude: Double?) {
        lastKnownLatitude = latitude
        lastKnownLongitude = longitude

        viewModelScope.launch(ioDispatcher) {
            _uiState.value = ListingUiState.Loading
            try {

                val listingsResult = try {
                    repository.getAvailableListings(latitude, longitude)
                } catch (e: Exception) {

                    Log.w("ListingViewModel", "Repository doesn't accept location parameters, using fallback", e)
                    repository.getAvailableListings(latitude, longitude)
                }

                val claimsResult = wasteClaimRepository.getWasteClaims()

                listingsResult.fold(
                    onSuccess = { listings ->
                        claimsResult.fold(
                            onSuccess = { claims ->
                                Log.d("API", "Fetched ${listings.size} listings and ${claims.size} claims")

                                val collectedListingIds = claims
                                    .filter { it.claimStatus == "collected" }
                                    .mapNotNull { it.listingId }
                                    .toSet()

                                val filteredListings = listings.filter { listing ->
                                    val productType = listing.productType?.lowercase()?.trim() ?: ""
                                    val status = listing.status?.lowercase()?.trim() ?: ""
                                    val listingId = listing.listingId
                                    val isInedible = productType == "inedible"
                                    val isAvailable = status == "available"
                                    val isNotCollected = listingId != null && !collectedListingIds.contains(listingId)
                                    isInedible && isAvailable && isNotCollected
                                }

                                val currentCount = filteredListings.size
                                if (_previousListingCount.value > 0 && currentCount > _previousListingCount.value) {
                                    _newListingDetected.value = true
                                }
                                _previousListingCount.value = currentCount

                                Log.d("API", "Filtered to ${filteredListings.size} available inedible listings")
                                _uiState.value = ListingUiState.Success(filteredListings)
                            },
                            onFailure = { exception ->
                                Log.e("API", "Failed to load waste claims", exception)
                                _uiState.value = ListingUiState.Error("Error loading waste claims: ${exception.message}")
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e("API", "Failed to load listings", exception)
                        _uiState.value = ListingUiState.Error("Error loading listings: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("API", "Unexpected error in fetchInedibleListings", e)
                _uiState.value = ListingUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun refreshListings() {
        _isRefreshing.value = true
        _newListingDetected.value = false
        fetchInedibleListings(lastKnownLatitude, lastKnownLongitude)
        _isRefreshing.value = false
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun claimListing(listingId: Int?) {
        if (listingId == null) return

        viewModelScope.launch(ioDispatcher) {
            try {
                val listing = when (val state = _uiState.value) {
                    is ListingUiState.Success -> state.listings.find { it.listingId == listingId }
                    else -> null
                }

                if (listing != null) {
                    val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                    val wasteClaim = WasteClaim(
                        listingId = listingId,
                        user = 18,
                        claimTime = currentTime,
                        claimStatus = "pending"
                    )

                    val result = wasteClaimRepository.addWasteClaim(wasteClaim)

                    result.fold(
                        onSuccess = {
                            _claimedListings.value = _claimedListings.value + listingId
                            _showClaimSuccessDialog.value = true
                            fetchInedibleListings(lastKnownLatitude, lastKnownLongitude)
                        },
                        onFailure = { exception ->
                            Log.e("API", "Failed to claim listing", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("API", "Failed to claim listing", e)
            }
        }
    }

    fun dismissClaimSuccessDialog() {
        _showClaimSuccessDialog.value = false
    }

    fun resetNewListingFlag() {
        _newListingDetected.value = false
    }
}


