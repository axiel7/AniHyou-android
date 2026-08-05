package com.axiel7.anihyou.feature.home

import app.cash.turbine.test
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.model.HomeTab
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val userRepository: UserRepository = mockk()
    private val defaultPreferencesRepository: DefaultPreferencesRepository = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { userRepository.getUnreadNotificationCount() } returns flowOf(5)
        viewModel = HomeViewModel(userRepository, defaultPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `unreadNotificationCount should emit values from userRepository`() = runTest {
        viewModel.unreadNotificationCount.test {
            assertEquals(5, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unreadNotificationCount should emit 0 when repository emits null`() = runTest {
        every { userRepository.getUnreadNotificationCount() } returns flowOf(null)
        // Re-initialize because flow is assigned at init
        viewModel = HomeViewModel(userRepository, defaultPreferencesRepository)
        
        viewModel.unreadNotificationCount.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveHomeTab should call defaultPreferencesRepository`() = runTest {
        val tabIndex = HomeTab.ACTIVITY_FEED.ordinal
        viewModel.saveHomeTab(tabIndex)

        coVerify { defaultPreferencesRepository.setDefaultHomeTab(HomeTab.ACTIVITY_FEED) }
    }
}
