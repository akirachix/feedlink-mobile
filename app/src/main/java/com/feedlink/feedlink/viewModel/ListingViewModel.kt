package com.feedlink.feedlink.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.model.Notification
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
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
    private val wasteClaimRepository: WasteClaimRepository
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

    init {
        fetchInedibleListings()
    }

    fun fetchInedibleListings() {
        viewModelScope.launch {
            _uiState.value = ListingUiState.Loading
            try {
                Log.d("API", "Fetching listings...")
                val result = repository.getAvailableListings()

                result.fold(
                    onSuccess = { listings ->
                        Log.d("API", "Fetched ${listings.size} listings")

                        val productTypes = listings.mapNotNull { it.productType }.distinct()
                        Log.d("API", "Available product types: $productTypes")

                        val categories = listings.mapNotNull { it.category }.distinct()
                        Log.d("API", "Available categories: $categories")

                        listings.forEach { listing ->
                            Log.d("API", "Item: ID=${listing.listingId}, ProductType='${listing.productType}', Category='${listing.category}', Description='${listing.description}'")
                        }

                        val inedibleListings = listings.filter { listing ->
                            val productType = listing.productType?.lowercase()?.trim() ?: ""

                            val isInedible = productType == "inedible"

                            Log.d("API", "Item ID=${listing.listingId}, ProductType='$productType', Category='${listing.category}', IsInedible=$isInedible")

                            isInedible
                        }

                        Log.d("API", "Filtered to ${inedibleListings.size} inedible listings")

                        if (inedibleListings.isEmpty()) {
                            Log.w("API", "No inedible items found! Available product types: $productTypes")
                        }

                        _uiState.value = ListingUiState.Success(inedibleListings)
                    },
                    onFailure = { exception ->
                        Log.e("API", "Failed to load listings", exception)
                        _uiState.value = ListingUiState.Error("Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("API", "Unexpected error", e)
                _uiState.value = ListingUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun refreshListings() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                Log.d("API", "Refreshing listings...")
                val result = repository.getAvailableListings()

                result.fold(
                    onSuccess = { listings ->
                        Log.d("API", "Refreshed ${listings.size} listings")

                        val inedibleListings = listings.filter { listing ->
                            val productType = listing.productType?.lowercase()?.trim() ?: ""

                            val isInedible = productType == "inedible"

                            Log.d("API", "Item ID=${listing.listingId}, ProductType='$productType', Category='${listing.category}', IsInedible=$isInedible")

                            isInedible
                        }

                        Log.d("API", "Filtered to ${inedibleListings.size} inedible listings")

                        if (inedibleListings.isEmpty()) {
                            Log.w("API", "No inedible items found! Available product types: ${listings.mapNotNull { it.productType }.distinct()}")
                        }

                        _uiState.value = ListingUiState.Success(inedibleListings)
                    },
                    onFailure = { exception ->
                        Log.e("API", "Failed to refresh listings", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("API", "Unexpected error during refresh", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun claimListing(listingId: Int?) {
        if (listingId != null) {
            viewModelScope.launch {
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
                            onSuccess = { claim ->
                                _claimedListings.value = _claimedListings.value + listingId
                                _showClaimSuccessDialog.value = true

                                val notification = Notification(
                                    id = java.util.UUID.randomUUID().toString(),
                                    title = "Waste Claimed",
                                    message = "You have successfully claimed ${listing.productType} waste",
                                    timestamp = System.currentTimeMillis(),
                                    isRead = false
                                )

                                Log.d("Notification", "Created notification: $notification")
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
    }

    fun dismissClaimSuccessDialog() {
        _showClaimSuccessDialog.value = false
    }
}