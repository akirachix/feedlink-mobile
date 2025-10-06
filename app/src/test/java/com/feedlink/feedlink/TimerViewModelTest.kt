package com.feedlink.feedlink.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.TimerViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: WasteClaimRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val claimId = 1

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()

        coEvery { mockRepository.getWasteClaimById(claimId) } returns Result.success(TestData.mockWasteClaims[0])
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWasteClaimSuccess() = runTest {

        val viewModel = TimerViewModel(mockRepository, claimId)
        val wasteClaim = viewModel.wasteClaim.value
        assertTrue(wasteClaim?.isSuccess == true)
        assertEquals(TestData.mockWasteClaims[0], wasteClaim?.getOrNull())
    }

    @Test
    fun onTimerExpiredUpdatesClaimStatus() = runTest {
        val updatedClaim = TestData.mockWasteClaims[0].copy(claimStatus = "claim")
        coEvery { mockRepository.updateClaimStatus(claimId, "claim") } returns Result.success(updatedClaim)

        val viewModel = TimerViewModel(mockRepository, claimId)
        viewModel.onTimerExpired()

        assertTrue(viewModel.timerExpired.value)
        val wasteClaim = viewModel.wasteClaim.value
        assertTrue(wasteClaim?.isSuccess == true)
        assertEquals(updatedClaim, wasteClaim?.getOrNull())
    }

    @Test
    fun getTimeLeftInSeconds() = runTest {
        val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val claim = TestData.mockWasteClaims[0].copy(claimTime = currentTime)
        coEvery { mockRepository.getWasteClaimById(claimId) } returns Result.success(claim)

        val viewModel = TimerViewModel(mockRepository, claimId)
        val timeLeft = viewModel.getTimeLeftInSeconds()

        assertTrue(timeLeft > 0)
    }
}