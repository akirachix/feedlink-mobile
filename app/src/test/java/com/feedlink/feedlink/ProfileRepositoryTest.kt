package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File

class ProfileRepositoryTest {

    private lateinit var mockApi: ApiInterface
    private lateinit var repository: ProfileRepository

    @Before
    fun setup() {
        mockApi = mockk(relaxed = true)
        repository = ProfileRepository(mockApi)
    }

    @Test
    fun testFetchUserProfileById() = runTest {
        val expectedResponse = Response.success(TestData.mockUserProfile)
        coEvery { mockApi.getUserProfileById(1) } returns expectedResponse

        val response = repository.fetchUserProfileById(1)

        TestCase.assertTrue(response.isSuccessful)
        TestCase.assertEquals(expectedResponse.body(), response.body())
    }

    @Test
    fun testUpdateProfileWithImage() = runTest {
        val file = File("semhal.jpg")
        val expectedResponse = Response.success(TestData.mockUserProfile)
        coEvery {
            mockApi.updateUserProfileWithImage(
                userId = 1,
                firstName = any(),
                lastName = any(),
                email = any(),
                address = any(),
                role = any(),
                tillNumber = any(),
                profilePicture = any()
            )
        } returns expectedResponse

        val response = repository.updateProfileWithImage(
            userId = 1,
            firstName = "semhal",
            lastName = "estif",
            email = "semhlaestif@gmail.com",
            address = "123 Main St",
            role = "User",
            tillNumber = "123456",
            imageFile = file
        )

        TestCase.assertTrue(response.isSuccessful)
        TestCase.assertEquals(expectedResponse.body(), response.body())
    }
}
