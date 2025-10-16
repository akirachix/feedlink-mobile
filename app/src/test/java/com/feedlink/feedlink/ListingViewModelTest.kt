package com.feedlink.feedlink.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.ListingRepository
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.ListingUiState
import com.feedlink.feedlink.viewmodel.ListingViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.v(any<String>(), any<String>()) } returns 0

        Dispatchers.setMain(testDispatcher)

        mockListingRepository = mockk()
        mockWasteClaimRepository = mockk()

        coEvery { mockListingRepository.getAvailableListings(latitude, longitude) } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(emptyList())

        viewModel = ListingViewModel(mockListingRepository, mockWasteClaimRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load listings successfully`() = runTest {
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ListingUiState.Success)
        val successState = uiState as ListingUiState.Success
        assertEquals(2, successState.listings.size)
    }

    @Test
    fun `fetchInedibleListingsSuccess should update uiState to Success`() = runTest {
        coEvery { mockListingRepository.getAvailableListings(latitude, longitude) } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(emptyList())

        viewModel.fetchInedibleListings(userLocation?.first, userLocation?.second)

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ListingUiState.Success)
        val successState = uiState as ListingUiState.Success
        assertEquals(2, successState.listings.size)
    }

    @Test
    fun `fetchInedibleListingsFailure should update uiState to Error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockListingRepository.getAvailableListings(latitude, longitude) } returns Result.failure(exception)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(emptyList())

        viewModel.fetchInedibleListings(userLocation?.first, userLocation?.second)

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ListingUiState.Error)
        val errorState = uiState as ListingUiState.Error
        assertEquals("Error loading listings: Network error", errorState.message)
    }

    @Test
    fun `claimListingSuccess should update claimedListings and show dialog`() = runTest {
        val listingId = 1
        coEvery { mockWasteClaimRepository.addWasteClaim(any<WasteClaim>()) } returns Result.success(TestData.mockWasteClaims[0])
        coEvery { mockListingRepository.getAvailableListings(latitude, longitude) } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(emptyList())

        viewModel.claimListing(listingId)

        assertTrue(viewModel.claimedListings.value.contains(listingId))
        assertTrue(viewModel.showClaimSuccessDialog.value)
    }

    @Test
    fun `dismissClaimSuccessDialog should hide the dialog`() = runTest {
        val listingId = 1
        coEvery { mockWasteClaimRepository.addWasteClaim(any<WasteClaim>()) } returns Result.success(TestData.mockWasteClaims[0])
        coEvery { mockListingRepository.getAvailableListings(latitude, longitude) } returns Result.success(TestData.mockListings)
        coEvery { mockWasteClaimRepository.getWasteClaims() } returns Result.success(emptyList())

        viewModel.claimListing(listingId)
        assertTrue(viewModel.showClaimSuccessDialog.value)

        viewModel.dismissClaimSuccessDialog()

        assertFalse(viewModel.showClaimSuccessDialog.value)
    }

    @Test
    fun `updateSearchQuery should update searchQuery state`() = runTest {
        val query = "Fruits"

        viewModel.updateSearchQuery(query)

        assertEquals(query, viewModel.searchQuery.value)
    }
}