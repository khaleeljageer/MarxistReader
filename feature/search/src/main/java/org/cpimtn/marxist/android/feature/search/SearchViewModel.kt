package org.cpimtn.marxist.android.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    getRecentSearchesFlowUseCase: GetRecentSearchesFlowUseCase,
    getPostsFlowUseCase: GetPostsFlowUseCase,
    getCategoriesWithCountFlowUseCase: GetCategoriesWithCountFlowUseCase,
    getTimelineMonthsFlowUseCase: GetTimelineMonthsFlowUseCase,
    getSearchResultsFlowUseCase: GetSearchResultsFlowUseCase,
    getSearchSuggestionsFlowUseCase: GetSearchSuggestionsFlowUseCase,
    getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    getPostsByCategoryFlowUseCase: GetPostsByCategoryFlowUseCase,
    getPostsByMonthFlowUseCase: GetPostsByMonthFlowUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _submittedQuery = MutableStateFlow<String?>(null)
    private val _activeFilter = MutableStateFlow<ActiveFilter?>(null)

    /**
     * Single shared subscription to the full posts table, reused by every post-derived
     * flow below (categories-with-count, timeline months, category/month browsing) so they
     * don't each independently re-run the same Room query on every DB change.
     */
    private val postsFlow: Flow<List<Post>> = getPostsFlowUseCase()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    /**
     * Submitted query paired atomically with its results, plus a distinguishable in-flight
     * state. Pairing them in one flow (rather than combining `_submittedQuery` and a
     * separately-derived results flow) avoids a stale read: combine() reacts to
     * `_submittedQuery` changing before the new query's results have arrived, which would
     * otherwise render a false "no results" flash using the previous query's (or no query's)
     * stale empty result list. [Phase.Loading] is emitted synchronously so the UI can show a
     * loading skeleton for the gap between submitting and the real results arriving.
     */
    private val submittedResultsFlow: Flow<Phase<String>> = _submittedQuery.flatMapLatest { q ->
        if (q != null) {
            getSearchResultsFlowUseCase(q, 50)
                .map<List<FeedItem>, Phase<String>> { Phase.Loaded(q, it) }
                .onStart { emit(Phase.Loading(q)) }
        } else {
            flowOf(Phase.Idle)
        }
    }

    private val suggestionsFlow = _query
        .debounce(300.milliseconds)
        .flatMapLatest { q ->
            if (q.isNotBlank()) getSearchSuggestionsFlowUseCase(q, 10) else flowOf(emptyList())
        }

    /**
     * Active category/month browse (from Discovery chip taps) paired atomically with its
     * results, for the same reason as [submittedResultsFlow] above.
     */
    private val filteredBrowseFlow: Flow<Phase<ActiveFilter>> = _activeFilter.flatMapLatest { filter ->
        when (filter) {
            is ActiveFilter.Category -> getPostsByCategoryFlowUseCase(postsFlow, filter.id)
                .map<List<FeedItem>, Phase<ActiveFilter>> { Phase.Loaded(filter, it) }
                .onStart { emit(Phase.Loading(filter)) }
            is ActiveFilter.Month -> getPostsByMonthFlowUseCase(postsFlow, filter.key)
                .map<List<FeedItem>, Phase<ActiveFilter>> { Phase.Loaded(filter, it) }
                .onStart { emit(Phase.Loading(filter)) }
            null -> flowOf(Phase.Idle)
        }
    }

    /**
     * The `uiState` combine below has 8 source flows. Kotlin's typed `combine` overloads only
     * go up to 5 arity — beyond that the vararg overload hands the transform an untyped
     * `Array<Any>`, forcing unchecked casts. Grouping the sources into two typed holder flows of
     * ≤5 each lets us combine those two with full type safety and no casts.
     */
    private data class DiscoveryInputs(
        val recent: List<String>,
        val categories: List<CategoryWithCount>,
        val timeline: List<TimelineMonth>,
        val suggestions: List<FeedItem>,
    )

    private data class ResultInputs(
        val query: String,
        val submittedPhase: Phase<String>,
        val savedIds: Set<Int>,
        val filteredPhase: Phase<ActiveFilter>,
    )

    private val discoveryInputsFlow: Flow<DiscoveryInputs> = combine(
        getRecentSearchesFlowUseCase(maxSize = 10),
        getCategoriesWithCountFlowUseCase(postsFlow),
        getTimelineMonthsFlowUseCase(postsFlow, maxEntries = 12),
        suggestionsFlow,
    ) { recent, categories, timeline, suggestions ->
        DiscoveryInputs(recent, categories, timeline, suggestions)
    }

    private val resultInputsFlow: Flow<ResultInputs> = combine(
        _query,
        submittedResultsFlow,
        getSavedPostIdsFlowUseCase(),
        filteredBrowseFlow,
    ) { query, submittedPhase, savedIds, filteredPhase ->
        ResultInputs(query, submittedPhase, savedIds, filteredPhase)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        discoveryInputsFlow,
        resultInputsFlow,
    ) { (recent, categories, timeline, suggestions), (query, submittedPhase, savedIds, filteredPhase) ->
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
            suggestions = suggestions.map(::feedItemToSearchItemUi),
        )
        when {
            submittedPhase is Phase.Loading -> SearchUiState.Searching(query = submittedPhase.value)
            submittedPhase is Phase.Loaded && submittedPhase.items.isEmpty() ->
                SearchUiState.NotFound(submittedPhase.value)
            submittedPhase is Phase.Loaded -> SearchUiState.Results(
                query = submittedPhase.value,
                items = submittedPhase.items,
                savedPostIds = savedIds,
            )
            filteredPhase is Phase.Loading -> SearchUiState.Searching(query = filteredPhase.value.label)
            filteredPhase is Phase.Loaded && filteredPhase.items.isEmpty() ->
                SearchUiState.NotFound(filteredPhase.value.label)
            filteredPhase is Phase.Loaded -> SearchUiState.Results(
                query = filteredPhase.value.label,
                items = filteredPhase.items,
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

        _activeFilter.value = null
        _submittedQuery.value = trimmed
        viewModelScope.launch {
            addRecentSearchUseCase(trimmed)
        }
    }

    /** Clear submitted search/filter and go back to discovery. */
    fun onClearSearch() {
        _submittedQuery.value = null
        _query.value = ""
        _activeFilter.value = null
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }

    fun toggleSave(postId: Int, isSaved: Boolean) {
        viewModelScope.launch {
            if (isSaved) unsavePostUseCase(postId) else savePostUseCase(postId)
        }
    }

    /** Tapping a category chip browses its posts, like submitting a search for it. */
    fun onCategoryClick(category: CategoryUi) {
        _activeFilter.update { current ->
            if (current is ActiveFilter.Category && current.id == category.id) {
                null
            } else {
                ActiveFilter.Category(id = category.id, label = category.name)
            }
        }
        _submittedQuery.value = null
        _query.value = ""
    }

    /** Tapping a timeline chip browses that month's posts, like submitting a search for it. */
    fun onTimelineMonthClick(month: TimelineMonthUi) {
        _activeFilter.update { current ->
            if (current is ActiveFilter.Month && current.key == month.key) {
                null
            } else {
                ActiveFilter.Month(key = month.key, label = month.label)
            }
        }
        _submittedQuery.value = null
        _query.value = ""
    }
}

/** A category/month browse triggered from Discovery, shown via [SearchUiState.Results]. */
private sealed interface ActiveFilter {
    val label: String

    data class Category(val id: Int, override val label: String) : ActiveFilter
    data class Month(val key: String, override val label: String) : ActiveFilter
}

/**
 * Tri-state wrapper for a query/filter-driven flow: [Idle] (nothing active), [Loading]
 * (active but its results haven't arrived yet — renders [SearchUiState.Searching]), or
 * [Loaded] (results arrived, possibly empty).
 */
private sealed interface Phase<out T> {
    data object Idle : Phase<Nothing>
    data class Loading<T>(val value: T) : Phase<T>
    data class Loaded<T>(val value: T, val items: List<FeedItem>) : Phase<T>
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
        val items: List<FeedItem>,
        val savedPostIds: Set<Int>,
    ) : SearchUiState

    /** Submitted search returned no results. */
    data class NotFound(val query: String) : SearchUiState

    /** A submitted query or category/month browse whose results haven't arrived yet. */
    data class Searching(val query: String) : SearchUiState
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
