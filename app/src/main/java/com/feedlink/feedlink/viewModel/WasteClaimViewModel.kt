package com.feedlink.feedlink.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.WasteClaimRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WasteClaimUiState {
    object Loading : WasteClaimUiState()
    data class Success(val claims: List<WasteClaim>) : WasteClaimUiState()
    data class Error(val message: String) : WasteClaimUiState()
}

class WasteClaimViewModel(
    private val repository: WasteClaimRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WasteClaimUiState>(WasteClaimUiState.Loading)
    val uiState: StateFlow<WasteClaimUiState> = _uiState

    init {
        fetchAllWasteClaims()
    }

    fun fetchAllWasteClaims() {
        viewModelScope.launch {
            _uiState.value = WasteClaimUiState.Loading
            try {
                val result = repository.getWasteClaims()
                result.fold(
                    onSuccess = { claims ->
                        _uiState.value = WasteClaimUiState.Success(claims)
                    },
                    onFailure = { exception ->
                        _uiState.value = WasteClaimUiState.Error("Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = WasteClaimUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
}