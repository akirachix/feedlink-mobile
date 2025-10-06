package com.feedlink.feedlink.viewModel

import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.TimerViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class TimerViewModelTest {

    private lateinit var repository: WasteClaimRepository
    private lateinit var viewModel: TimerViewModel

    private val claimId = 1
    private val listingId = 100

    private val now = Date()
    private val claimTime = Date(now.time - 1000)
    private val pickupDeadline = Date(now.time + 3600000)

    private val mockClaim = WasteClaim(
        wasteId = claimId,
        listingId = listingId,
        claimTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(claimTime),
        claimStatus = "pending"
    )

    private val mockListing = Listing(
        productType = "edible",
        category = "Dairy",
        description = "Natural",
        quantity = "10.00",
        listingId = listingId,
        originalPrice = 97f,
        discountedPrice = 80f,
        expiryDate = "2025-10-04T05:52:00Z",
        image = "https://example.com/image.jpg",
        imageUrl = null,
        status = "available",
        createdAt = "2025-10-06T05:52:36.976179Z",
        updatedAt = "2025-10-06T05:52:36.976191Z",
        uploadMethod = "manual",
        pickupWindowDuration = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(pickupDeadline),
        unit = "L",
        producer = 23
    )

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0

        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        repository = mockk(relaxed = true)

        coEvery { repository.getWasteClaimById(claimId) } returns Result.success(mockClaim)
        coEvery { repository.getListingById(listingId) } returns Result.success(mockListing)

        viewModel = TimerViewModel(repository, claimId)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun fetchWasteClaimSuccess() = runTest {
        advanceUntilIdle()

        clearMocks(repository, answers = false, recordedCalls = true)

        coEvery { repository.getWasteClaimById(claimId) } returns Result.success(mockClaim)
        coEvery { repository.getListingById(listingId) } returns Result.success(mockListing)

        viewModel.refresh()

        advanceUntilIdle()

        coVerify(atLeast = 1) { repository.getWasteClaimById(claimId) }
        coVerify(atLeast = 1) { repository.getListingById(listingId) }
        assert(viewModel.wasteClaim.value?.isSuccess == true)
        assert(viewModel.listing.value?.isSuccess == true)
        assert(viewModel.pickupDeadline.value != null)
    }

    @Test
    fun getTimeLeftInSeconds() = runTest {
        advanceUntilIdle()

        val timeLeft = viewModel.getTimeLeftInSeconds()
        assert(timeLeft > 0)
    }

    @Test
    fun onTimerExpiredUpdatesClaimStatus() = runTest {
        advanceUntilIdle()

        val updatedClaim = mockClaim.copy(claimStatus = "claim")
        coEvery { repository.updateClaimStatus(claimId, "claim") } returns Result.success(updatedClaim)

        viewModel.onTimerExpired()

        advanceUntilIdle()

        coVerify(exactly = 1) { repository.updateClaimStatus(claimId, "claim") }
        assert(viewModel.timerExpired.value == true)
        assert(viewModel.wasteClaim.value?.getOrNull()?.claimStatus == "claim")
    }
}