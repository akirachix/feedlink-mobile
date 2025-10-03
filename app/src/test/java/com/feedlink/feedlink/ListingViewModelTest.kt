package com.feedlink.feedlink.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListingViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ListingViewModel
    private lateinit var mockListingRepository: ListingRepository
    private lateinit var mockWasteClaimRepository: WasteClaimRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockListingRepository = mockk()
        mockWasteClaimRepository = mockk()
        viewModel = ListingViewModel(mockListingRepository, mockWasteClaimRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchInedibleListingsSuccess() = runTest {
        coEvery { mockListingRepository.getAvailableListings() } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel.fetchInedibleListings()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ListingUiState.Success)
        val successState = uiState as ListingUiState.Success
        assertEquals(2, successState.listings.size)
    }

    @Test
    fun fetchInedibleListingsFailure() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockListingRepository.getAvailableListings() } returns Result.failure(exception)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel.fetchInedibleListings()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ListingUiState.Error)
        val errorState = uiState as ListingUiState.Error
        assertEquals("Error loading listings: Network error", errorState.message)
    }

    @Test
    fun claimListingSuccess() = runTest {
        val listingId = 1
        coEvery { mockWasteClaimRepository.addWasteClaim(any()) } returns Result.success(TestData.mockWasteClaims[0])
        coEvery { mockListingRepository.getAvailableListings() } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel.claimListing(listingId)

        assertTrue(viewModel.claimedListings.value.contains(listingId))
        assertTrue(viewModel.showClaimSuccessDialog.value)
    }

    @Test
    fun dismissClaimSuccessDialog() = runTest {
        val listingId = 1
        coEvery { mockWasteClaimRepository.addWasteClaim(any()) } returns Result.success(TestData.mockWasteClaims[0])
        coEvery { mockListingRepository.getAvailableListings() } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel.claimListing(listingId)
        assertTrue(viewModel.showClaimSuccessDialog.value)

        viewModel.dismissClaimSuccessDialog()

        assertFalse(viewModel.showClaimSuccessDialog.value)
    }

    @Test
    fun updateSearchQuery() = runTest {
        val query = "Fruits"

        viewModel.updateSearchQuery(query)

        assertEquals(query, viewModel.searchQuery.value)
    }
}