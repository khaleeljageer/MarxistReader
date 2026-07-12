package org.cpimtn.marxist.android.feature.search

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cpimtn.marxist.android.domain.model.Category
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.model.TimelineMonth
import org.cpimtn.marxist.android.domain.usecase.AddRecentSearchUseCase
import org.cpimtn.marxist.android.domain.usecase.ClearRecentSearchesUseCase
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesWithCountFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsByCategoryFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsByMonthFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetRecentSearchesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchResultsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchSuggestionsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTimelineMonthsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for SearchViewModel. Mocks use cases; sets Main to test dispatcher so
 * viewModelScope (including the shared postsFlow) advances with runTest.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val getRecentSearchesFlowUseCase: GetRecentSearchesFlowUseCase = mockk()
    private val getPostsFlowUseCase: GetPostsFlowUseCase = mockk()
    private val getCategoriesWithCountFlowUseCase: GetCategoriesWithCountFlowUseCase = mockk()
    private val getTimelineMonthsFlowUseCase: GetTimelineMonthsFlowUseCase = mockk()
    private val getSearchResultsFlowUseCase: GetSearchResultsFlowUseCase = mockk()
    private val getSearchSuggestionsFlowUseCase: GetSearchSuggestionsFlowUseCase = mockk()
    private val getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase = mockk()
    private val getPostsByCategoryFlowUseCase: GetPostsByCategoryFlowUseCase = mockk()
    private val getPostsByMonthFlowUseCase: GetPostsByMonthFlowUseCase = mockk()
    private val addRecentSearchUseCase: AddRecentSearchUseCase = mockk()
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase = mockk()
    private val savePostUseCase: SavePostUseCase = mockk()
    private val unsavePostUseCase: UnsavePostUseCase = mockk()

    private fun post(id: Int, categories: List<Int> = emptyList()) =
        Post(id, "2026-07-01T00:00:00", "slug-$id", "Title $id", "Content $id", "Excerpt $id", emptyList(), categories)

    private fun feedItem(id: Int) = FeedItem(post(id), "", emptyList())

    private fun stubDefaults(
        recent: List<String> = emptyList(),
        categories: List<CategoryWithCount> = emptyList(),
        timeline: List<TimelineMonth> = emptyList(),
        searchResults: List<FeedItem> = emptyList(),
        suggestions: List<FeedItem> = emptyList(),
        savedIds: Set<Int> = emptySet(),
        categoryResults: List<FeedItem> = emptyList(),
        monthResults: List<FeedItem> = emptyList(),
    ) {
        every { getRecentSearchesFlowUseCase(maxSize = 10) } returns flowOf(recent)
        every { getPostsFlowUseCase() } returns flowOf(emptyList())
        every { getCategoriesWithCountFlowUseCase(any()) } returns flowOf(categories)
        every { getTimelineMonthsFlowUseCase(any(), any()) } returns flowOf(timeline)
        every { getSearchResultsFlowUseCase(any(), any()) } returns flowOf(searchResults)
        every { getSearchSuggestionsFlowUseCase(any(), any()) } returns flowOf(suggestions)
        every { getSavedPostIdsFlowUseCase() } returns flowOf(savedIds)
        every { getPostsByCategoryFlowUseCase(any(), any()) } returns flowOf(categoryResults)
        every { getPostsByMonthFlowUseCase(any(), any()) } returns flowOf(monthResults)
        coEvery { addRecentSearchUseCase(any()) } just Runs
    }

    private fun createViewModel(): SearchViewModel = SearchViewModel(
        getRecentSearchesFlowUseCase,
        getPostsFlowUseCase,
        getCategoriesWithCountFlowUseCase,
        getTimelineMonthsFlowUseCase,
        getSearchResultsFlowUseCase,
        getSearchSuggestionsFlowUseCase,
        getSavedPostIdsFlowUseCase,
        getPostsByCategoryFlowUseCase,
        getPostsByMonthFlowUseCase,
        addRecentSearchUseCase,
        clearRecentSearchesUseCase,
        savePostUseCase,
        unsavePostUseCase,
    )

    @Test
    fun initial_state_with_no_query_is_discovery() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val category = CategoryWithCount(Category(1, "Philosophy"), 5)
        val month = TimelineMonth("Jul 2026", "2026-07")
        stubDefaults(recent = listOf("dialectics"), categories = listOf(category), timeline = listOf(month))

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Discovery)
        state as SearchUiState.Discovery
        assertEquals(listOf("dialectics"), state.recentSearches)
        assertEquals(1, state.categories.size)
        assertEquals("Philosophy", state.categories[0].name)
        assertEquals(1, state.timelineMonths.size)
    }

    @Test
    fun submit_search_with_results_shows_results_state() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults(searchResults = listOf(feedItem(1), feedItem(2)))

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.onSearchSubmit("dialectics")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Results)
        state as SearchUiState.Results
        assertEquals("dialectics", state.query)
        assertEquals(2, state.items.size)
        coVerify { addRecentSearchUseCase("dialectics") }
    }

    @Test
    fun submit_search_shows_searching_before_results_arrive() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults()
        every { getSearchResultsFlowUseCase(any(), any()) } returns flow {
            delay(100)
            emit(listOf(feedItem(1)))
        }

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onSearchSubmit("dialectics")
        runCurrent()

        val searchingState = viewModel.uiState.value
        assertTrue(searchingState is SearchUiState.Searching)
        assertEquals("dialectics", (searchingState as SearchUiState.Searching).query)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Results)
    }

    @Test
    fun submit_search_with_no_results_shows_not_found_state() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults(searchResults = emptyList())

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.onSearchSubmit("nonexistent")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.NotFound)
        assertEquals("nonexistent", (state as SearchUiState.NotFound).query)
    }

    @Test
    fun submit_blank_query_is_a_no_op() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults()

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.onSearchSubmit("   ")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Discovery)
        coVerify(exactly = 0) { addRecentSearchUseCase(any()) }
    }

    @Test
    fun clear_search_returns_to_discovery() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults(searchResults = listOf(feedItem(1)))

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.onSearchSubmit("dialectics")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is SearchUiState.Results)

        viewModel.onClearSearch()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Discovery)
    }

    @Test
    fun category_click_shows_results_and_clicking_again_clears_it() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults(categoryResults = listOf(feedItem(1)))

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        val category = CategoryUi(id = 3, name = "History", articleCount = 4)

        viewModel.onCategoryClick(category)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Results)
        assertEquals("History", (state as SearchUiState.Results).query)

        viewModel.onCategoryClick(category)
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Discovery)
    }

    @Test
    fun category_click_shows_searching_before_results_arrive() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults()
        every { getPostsByCategoryFlowUseCase(any(), any()) } returns flow {
            delay(100)
            emit(listOf(feedItem(1)))
        }

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        val category = CategoryUi(id = 3, name = "History", articleCount = 4)

        viewModel.onCategoryClick(category)
        runCurrent()

        val searchingState = viewModel.uiState.value
        assertTrue(searchingState is SearchUiState.Searching)
        assertEquals("History", (searchingState as SearchUiState.Searching).query)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Results)
    }

    @Test
    fun timeline_month_click_with_no_posts_shows_not_found() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults(monthResults = emptyList())

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        val month = TimelineMonthUi(label = "Jul 2026", key = "2026-07")

        viewModel.onTimelineMonthClick(month)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.NotFound)
        assertEquals("Jul 2026", (state as SearchUiState.NotFound).query)
    }

    @Test
    fun toggle_save_when_not_saved_calls_save_use_case() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults()
        coEvery { savePostUseCase(any()) } just Runs

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.toggleSave(postId = 7, isSaved = false)
        advanceUntilIdle()

        coVerify { savePostUseCase(7) }
        coVerify(exactly = 0) { unsavePostUseCase(any()) }
    }

    @Test
    fun toggle_save_when_saved_calls_unsave_use_case() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        stubDefaults()
        coEvery { unsavePostUseCase(any()) } just Runs

        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        viewModel.toggleSave(postId = 7, isSaved = true)
        advanceUntilIdle()

        coVerify { unsavePostUseCase(7) }
        coVerify(exactly = 0) { savePostUseCase(any()) }
    }
}
