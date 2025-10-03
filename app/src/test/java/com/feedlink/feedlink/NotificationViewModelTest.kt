package com.feedlink.feedlink.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.feedlink.feedlink.TestData
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
class NotificationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NotificationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotificationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchNotificationsSuccess() = runTest {
        viewModel.fetchNotifications()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is NotificationUiState.Success)
        val successState = uiState as NotificationUiState.Success
        assertEquals(0, successState.notifications.size)
    }

    @Test
    fun addNotification() = runTest {
        val notification = TestData.mockNotifications[0]

        viewModel.addNotification(notification)

        val notifications = viewModel.notifications.value
        assertEquals(1, notifications.size)
        assertEquals(notification, notifications[0])
    }

    @Test
    fun markAsRead() = runTest {
        val notification = TestData.mockNotifications[0].copy(isRead = false)
        viewModel.addNotification(notification)

        viewModel.markAsRead(notification.id)

        val notifications = viewModel.notifications.value
        assertTrue(notifications[0].isRead)
    }
}