package com.feedlink.feedlink.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.UiState
import com.feedlink.feedlink.repository.ListingsRepository
import kotlinx.coroutines.launch


class ListingsViewModel(
    private val repository: ListingsRepository
) : ViewModel() {

    val listings = mutableStateOf<List<Listing>?>(null)
    val uiState = mutableStateOf(UiState())

    fun fetchListings(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            try {
                Log.d(
                    "ListingsViewModel",
                    "Fetching listings with lat: $latitude, long: $longitude"
                )
                val response = repository.fetchListings(latitude, longitude)
                if (response.isSuccessful) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        success = "Listings fetched successfully"
                    )
                    listings.value = response.body()
                } else {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = response.errorBody()?.string()
                    )
                }
            } catch (e: Exception) {
                Log.e("ListingsViewModel", "Error fetching listings", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}