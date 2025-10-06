package com.feedlink.feedlink.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.WasteClaimUiState
import com.feedlink.feedlink.viewmodel.WasteClaimViewModel
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
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.v(any<String>(), any<String>()) } returns 0

        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        coEvery { mockRepository.getWasteClaims() } returns Result.success(TestData.mockWasteClaims)

        viewModel = WasteClaimViewModel(mockRepository, testDispatcher)
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