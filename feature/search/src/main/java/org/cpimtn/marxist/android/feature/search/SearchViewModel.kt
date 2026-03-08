package org.cpimtn.marxist.android.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.domain.model.TimelineMonth
import org.cpimtn.marxist.android.domain.usecase.AddRecentSearchUseCase
import org.cpimtn.marxist.android.domain.usecase.ClearRecentSearchesUseCase
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesWithCountFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetRecentSearchesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTimelineMonthsFlowUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getRecentSearchesFlowUseCase: GetRecentSearchesFlowUseCase,
    getCategoriesWithCountFlowUseCase: GetCategoriesWithCountFlowUseCase,
    getTimelineMonthsFlowUseCase: GetTimelineMonthsFlowUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
) : ViewModel() {

    // Local state for selections (not from Room)
    private val _selectedMonth = MutableStateFlow<String?>(null)

    /**
     * Single combined UI state from 3 Room flows + local state.
     *
     * All 3 Room flows emit independently; combine() merges them
     * into one consistent snapshot for the UI.
     */
    val uiState: StateFlow<SearchUiState> = combine(
        getRecentSearchesFlowUseCase(maxSize = 10),
        getCategoriesWithCountFlowUseCase(),
        getTimelineMonthsFlowUseCase(maxEntries = 12),
        _selectedMonth,
    ) { recent, categories, timeline, month ->
        SearchUiState.Success(
            recentSearches = recent,
            categoriesWithCount = categories,
            timelineMonths = timeline,
            selectedMonth = month,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState.Loading,
    )

    fun onQueryChanged(query: String) {
        // TODO: Debounce and trigger FTS search
        // _query.value = query
    }

    fun onSearchSubmit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            addRecentSearchUseCase(trimmed)
        }

        // TODO: Trigger full search and navigate to results
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

        // TODO: Filter articles by selected month
    }
}

sealed interface SearchUiState {
    data object Loading : SearchUiState

    /**
     * Idle/Discovery state — no active search.
     * Shows recent searches, categories, timeline.
     */
    data class Discovery(
        val recentSearches: List<String> = emptyList(),
        val categories: List<CategoryUi> = emptyList(),
        val timelineMonths: List<TimelineMonthUi> = emptyList(),
        val selectedMonth: String? = null,
        val suggestions: List<SearchItemUi> = emptyList(),
    ) : SearchUiState

    /**
     * Active search results.
     */
    data class Results(
        val query: String,
        val items: List<SearchItemUi>,
        val savedPostIds: Set<Int>,
    ) : SearchUiState

    data object NotFound : SearchUiState
    data class Error(val message: String) : SearchUiState
}

data class SearchItemUi(
    val postId: Int,
    val title: String,
    val excerpt: String,          // Already stripped of HTML
    val formattedDate: String,    // Already formatted
    val categoryLabel: String,
    val tagLabels: List<String>,
    val readTime: String,         // "5 நிமி"
)

data class CategoryUi(
    val id: Int,
    val name: String,
    val articleCount: Int,
)

data class TimelineMonthUi(
    val label: String,            // "பிப் 2026"
    val year: Int,
    val month: Int,
)