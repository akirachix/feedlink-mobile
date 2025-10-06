package com.feedlink.feedlink.repository

import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.WasteClaim
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WasteClaimRepositoryTest {
    private lateinit var mockApiInterface: ApiInterface
    private lateinit var mockRepository: WasteClaimRepository

    @Before
    fun setup() {
        mockApiInterface = mockk(relaxed = true)
        mockRepository = WasteClaimRepository(mockApiInterface)
    }

    @Test
    fun testGetWasteClaimsSuccess() = runTest {
        coEvery { mockApiInterface.getWasteClaims() } returns TestData.mockWasteClaims

        val result = mockRepository.getWasteClaims()

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(TestData.mockWasteClaims, result.getOrNull())
    }

    @Test
    fun testGetWasteClaimByIdSuccess() = runTest {
        val claimId = 1
        coEvery { mockApiInterface.getWasteClaimById(claimId) } returns TestData.mockWasteClaims[0]

        val result = mockRepository.getWasteClaimById(claimId)

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(TestData.mockWasteClaims[0], result.getOrNull())
    }

    @Test
    fun testAddWasteClaimSuccess() = runTest {
        val newClaim = WasteClaim(
            listingId = 1,
            user = 18,
            claimTime = "2023-11-17T10:00:00",
            claimStatus = "pending"
        )
        coEvery { mockApiInterface.createWasteClaim(newClaim) } returns TestData.mockWasteClaims[0]

        val result = mockRepository.addWasteClaim(newClaim)

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(TestData.mockWasteClaims[0], result.getOrNull())
    }

    @Test
    fun testUpdateClaimStatusSuccess() = runTest {
        val claimId = 1
        val status = "collected"
        coEvery { mockApiInterface.updateClaimStatus(claimId, TestData.mockStatusRequest) } returns
                TestData.mockWasteClaims[0].copy(claimStatus = status)

        val result = mockRepository.updateClaimStatus(claimId, status)

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(status, result.getOrNull()?.claimStatus)
    }
}