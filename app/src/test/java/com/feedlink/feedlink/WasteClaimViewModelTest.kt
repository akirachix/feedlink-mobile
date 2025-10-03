package com.feedlink.feedlink.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WasteClaimViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WasteClaimViewModel
    private lateinit var mockRepository: WasteClaimRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = WasteClaimViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchAllWasteClaimsSuccess() = runTest {
        coEvery { mockRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel.fetchAllWasteClaims()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is WasteClaimUiState.Success)
        val successState = uiState as WasteClaimUiState.Success
        assertEquals(2, successState.claims.size)
    }

    @Test
    fun fetchAllWasteClaimsFailure() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockRepository.getWasteClaims() } returns Result.failure(exception)

        viewModel.fetchAllWasteClaims()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is WasteClaimUiState.Error)
        val errorState = uiState as WasteClaimUiState.Error
        assertEquals("Error: Network error", errorState.message)
    }
}