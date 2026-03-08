package org.cpimtn.marxist.android.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun SearchScreenRoute(
    onArticleClick: (postId: Int) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val colors = MarxistReaderTheme.colors

    when (val state = uiState) {
        SearchUiState.Loading -> SearchLoadingSkeleton()
        is SearchUiState.NotFound -> Column(modifier = Modifier.fillMaxSize()) {
            SearchBarWithBack(
                query = state.query,
                onBack = viewModel::onClearSearch,
                onQueryChange = viewModel::onQueryChanged,
                onSubmit = { viewModel.onSearchSubmit(query) },
                onClear = { viewModel.onQueryChanged("") },
                colors = colors,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            NoResultsMessage(query = state.query, modifier = Modifier.fillMaxSize())
        }
        is SearchUiState.Discovery -> SearchDiscovery(
            state = state,
            query = query,
            onQueryChange = viewModel::onQueryChanged,
            onSubmit = { viewModel.onSearchSubmit(query) },
            onClearSearch = viewModel::onClearSearch,
            onRecentSearchClick = { term -> viewModel.onSearchSubmit(term) },
            onClearRecentSearches = viewModel::clearRecentSearches,
            onTimelineMonthClick = viewModel::onTimelineMonthClick,
            onCategoryClick = { /* TODO: navigate to category */ },
            onArticleClick = onArticleClick,
        )
        is SearchUiState.Results -> Column(modifier = Modifier.fillMaxSize()) {
            SearchBarWithBack(
                query = state.query,
                onBack = viewModel::onClearSearch,
                onQueryChange = viewModel::onQueryChanged,
                onSubmit = { viewModel.onSearchSubmit(query) },
                onClear = { viewModel.onQueryChanged("") },
                colors = colors,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            if (state.items.isEmpty()) {
                NoResultsMessage(query = state.query, modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                ) {
                    items(
                        items = state.items,
                        key = { it.postId },
                    ) { item ->
                        SearchResultRow(
                            item = item,
                            isSaved = item.postId in state.savedPostIds,
                            onArticleClick = { onArticleClick(item.postId) },
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    }
                }
            }
        }
        is SearchUiState.Error -> SearchErrorMessage(
            message = state.message,
            onRetry = { viewModel.onSearchSubmit(query) },
        )
    }
}
