package com.feedlink.feedlink.repository

import com.feedlink.feedlink.TestData
import com.feedlink.feedlink.api.ApiInterface
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ListingRepositoryTest {
    private lateinit var mockApiInterface: ApiInterface
    private lateinit var mockRepository: ListingRepository

    @Before
    fun setup() {
        mockApiInterface = mockk(relaxed = true)
        mockRepository = ListingRepository(mockApiInterface)
    }

    @Test
    fun testGetAvailableListingsSuccess() = runTest {
        coEvery { mockApiInterface.getAvailableListings() } returns TestData.mockListings

        val result = mockRepository.getAvailableListings()

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(TestData.mockListings, result.getOrNull())
    }

    @Test
    fun testGetAvailableListingsFailure() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockApiInterface.getAvailableListings() } throws exception

        val result = mockRepository.getAvailableListings()

        TestCase.assertTrue(result.isFailure)
        TestCase.assertEquals(exception, result.exceptionOrNull())
    }
}