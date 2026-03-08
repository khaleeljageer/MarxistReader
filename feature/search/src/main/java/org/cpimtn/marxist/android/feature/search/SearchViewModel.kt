package org.cpimtn.marxist.android.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.TimelineMonth
import org.cpimtn.marxist.android.domain.usecase.AddRecentSearchUseCase
import org.cpimtn.marxist.android.domain.usecase.ClearRecentSearchesUseCase
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesWithCountFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetRecentSearchesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchResultsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchSuggestionsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTimelineMonthsFlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    getRecentSearchesFlowUseCase: GetRecentSearchesFlowUseCase,
    getCategoriesWithCountFlowUseCase: GetCategoriesWithCountFlowUseCase,
    getTimelineMonthsFlowUseCase: GetTimelineMonthsFlowUseCase,
    getSearchResultsFlowUseCase: GetSearchResultsFlowUseCase,
    getSearchSuggestionsFlowUseCase: GetSearchSuggestionsFlowUseCase,
    getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _submittedQuery = MutableStateFlow<String?>(null)
    private val _selectedMonth = MutableStateFlow<String?>(null)

    private val searchResultsFlow = _submittedQuery.flatMapLatest { q ->
        if (q != null) getSearchResultsFlowUseCase(q, 50) else flowOf(emptyList())
    }

    private val suggestionsFlow = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isNotBlank()) getSearchSuggestionsFlowUseCase(q, 10) else flowOf(emptyList())
        }

    val uiState: StateFlow<SearchUiState> = combine(
        getRecentSearchesFlowUseCase(maxSize = 10),
        getCategoriesWithCountFlowUseCase(),
        getTimelineMonthsFlowUseCase(maxEntries = 12),
        _selectedMonth,
        _query,
        _submittedQuery,
        searchResultsFlow,
        suggestionsFlow,
        getSavedPostIdsFlowUseCase(),
    ) { values ->
        val recent = values[0] as List<String>
        val categories = values[1] as List<CategoryWithCount>
        val timeline = values[2] as List<TimelineMonth>
        val month = values[3] as String?
        val query = values[4] as String
        val submitted = values[5] as String?
        val results = values[6] as List<FeedItem>
        val suggestions = values[7] as List<FeedItem>
        val savedIds = values[8] as Set<Int>
        val discovery = SearchUiState.Discovery(
            recentSearches = recent,
            categories = categories.map { item ->
                CategoryUi(
                    id = item.category.id,
                    name = item.category.name,
                    articleCount = item.postCount,
                )
            },
            timelineMonths = timeline.map { item ->
                TimelineMonthUi(
                    label = item.label,
                    key = item.key,
                )
            },
            selectedMonth = month,
            suggestions = suggestions.map(::feedItemToSearchItemUi),
        )
        when {
            submitted != null && results.isEmpty() -> SearchUiState.NotFound(submitted)
            submitted != null -> SearchUiState.Results(
                query = submitted,
                items = results.map(::feedItemToSearchItemUi),
                savedPostIds = savedIds,
            )
            else -> discovery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState.Loading,
    )

    val searchQuery: StateFlow<String> = _query

    private fun feedItemToSearchItemUi(f: FeedItem): SearchItemUi =
        SearchItemUi(
            postId = f.post.id,
            title = f.post.title,
            excerpt = f.post.excerpt,
            formattedDate = f.post.formattedDate,
            categoryLabel = f.categoryLabel,
            tagLabels = f.tagLabels,
            readTime = f.readTime,
        )

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun onSearchSubmit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        _submittedQuery.value = trimmed
        viewModelScope.launch {
            addRecentSearchUseCase(trimmed)
        }
    }

    /** Clear submitted search and go back to discovery. */
    fun onClearSearch() {
        _submittedQuery.value = null
        _query.value = ""
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }

    fun onTimelineMonthClick(month: String) {
        _selectedMonth.update { current ->
            if (current == month) null else month
        }
    }
}

sealed interface SearchUiState {
    data object Loading : SearchUiState

    /**
     * Idle/Discovery state — no active search.
     * Shows recent searches, categories, timeline, and optional suggestions while typing.
     */
    data class Discovery(
        val recentSearches: List<String> = emptyList(),
        val categories: List<CategoryUi> = emptyList(),
        val timelineMonths: List<TimelineMonthUi> = emptyList(),
        val selectedMonth: String? = null,
        val suggestions: List<SearchItemUi> = emptyList(),
    ) : SearchUiState

    /**
     * Active search results (or empty when no matches).
     */
    data class Results(
        val query: String,
        val items: List<SearchItemUi>,
        val savedPostIds: Set<Int>,
    ) : SearchUiState

    /** Submitted search returned no results. */
    data class NotFound(val query: String) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

data class SearchItemUi(
    val postId: Int,
    val title: String,
    val excerpt: String,
    val formattedDate: String,
    val categoryLabel: String,
    val tagLabels: List<String>,
    val readTime: String,
)

data class CategoryUi(
    val id: Int,
    val name: String,
    val articleCount: Int,
)

data class TimelineMonthUi(
    val label: String,
    val key: String,
)
