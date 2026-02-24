package org.cpimtn.marxist.android.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    val recentSearches: StateFlow<List<String>> = getRecentSearchesFlowUseCase(maxSize = 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoriesWithCount: StateFlow<List<CategoryWithCount>> = getCategoriesWithCountFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timelineMonths: StateFlow<List<TimelineMonth>> = getTimelineMonthsFlowUseCase(maxEntries = 12)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onRecentSearchClick(query: String) {
        viewModelScope.launch {
            addRecentSearchUseCase(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }

    fun onSearchSubmit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            viewModelScope.launch {
                addRecentSearchUseCase(trimmed)
            }
        }
    }
}
