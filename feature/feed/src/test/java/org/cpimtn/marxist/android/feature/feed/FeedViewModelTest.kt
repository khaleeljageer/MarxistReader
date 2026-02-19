package org.cpimtn.marxist.android.feature.feed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.StandardTestDispatcher
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.model.SyncStatus
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

/**
 * Unit tests for FeedViewModel. Mocks use cases; sets Main to test dispatcher so viewModelScope advances with runTest.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val getFeedItemsFlowUseCase: GetFeedItemsFlowUseCase = mockk()
    private val getSyncStatusUseCase: GetSyncStatusUseCase = mockk()
    private val getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase = mockk()
    private val savePostUseCase: SavePostUseCase = mockk()
    private val unsavePostUseCase: UnsavePostUseCase = mockk()
    private val syncPostsUseCase: SyncPostsUseCase = mockk()

    private fun createViewModel(): FeedViewModel = FeedViewModel(
        getFeedItemsFlowUseCase,
        getSavedPostIdsFlowUseCase,
        savePostUseCase,
        unsavePostUseCase,
        syncPostsUseCase,
    )

    @Test
    fun initial_collection_emits_empty_then_state_is_empty() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        every { getFeedItemsFlowUseCase() } returns flowOf(emptyList())
        every { getSyncStatusUseCase() } returns flowOf(SyncStatus())
        every { getSavedPostIdsFlowUseCase() } returns flowOf(emptySet())
        coEvery { syncPostsUseCase() } returns SyncResult.Success

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.feedUiState.value is FeedUiState.Empty)
    }

    @Test
    fun initial_collection_emits_posts_then_state_is_success() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val post = Post(1, "2024-01-01", "s", "Title", "Excerpt", emptyList(), emptyList())
        val feedItems = listOf(FeedItem(post, "", emptyList()))
        every { getFeedItemsFlowUseCase() } returns flowOf(feedItems)
        every { getSyncStatusUseCase() } returns flowOf(SyncStatus())
        every { getSavedPostIdsFlowUseCase() } returns flowOf(emptySet())
        coEvery { syncPostsUseCase() } returns SyncResult.Success

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.feedUiState.value
        assertTrue(state is FeedUiState.Success)
        assertEquals(feedItems, (state as FeedUiState.Success).feedItems)
    }

    @Test
    fun refresh_on_network_error_sets_error_state() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        every { getFeedItemsFlowUseCase() } returns flowOf(emptyList())
        every { getSyncStatusUseCase() } returns flowOf(SyncStatus())
        every { getSavedPostIdsFlowUseCase() } returns flowOf(emptySet())
        coEvery { syncPostsUseCase() } returns SyncResult.NetworkError("offline")

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.feedUiState.value
        assertTrue(state is FeedUiState.Error)
        assertEquals("offline", (state as FeedUiState.Error).message)
    }

    @Test
    fun refresh_on_success_does_not_set_error_state() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val post = Post(1, "2024-01-01", "s", "Title", "Excerpt", emptyList(), emptyList())
        val feedItems = listOf(FeedItem(post, "", emptyList()))
        every { getFeedItemsFlowUseCase() } returns flowOf(feedItems)
        every { getSyncStatusUseCase() } returns flowOf(SyncStatus())
        every { getSavedPostIdsFlowUseCase() } returns flowOf(emptySet())
        coEvery { syncPostsUseCase() } returns SyncResult.Success

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.refresh()
        advanceUntilIdle()

        // On Success the ViewModel leaves state to the flow; mock flow already emitted so we may stay Loading. Just ensure we didn't set Error.
        assertTrue(viewModel.feedUiState.value !is FeedUiState.Error)
    }
}
