package org.cpimtn.marxist.android.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.ui.common.ArticleListItem
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun SearchScreenRoute(
    onArticleClick: (postId: Int) -> Unit = {},
    showHelpIcon: Boolean = true,
    onHelpClick: () -> Unit = {},
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
        is SearchUiState.Searching -> Column(modifier = Modifier.fillMaxSize()) {
            SearchBarWithBack(
                query = state.query,
                onBack = viewModel::onClearSearch,
                onQueryChange = viewModel::onQueryChanged,
                onSubmit = { viewModel.onSearchSubmit(query) },
                onClear = { viewModel.onQueryChanged("") },
                colors = colors,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            SearchResultsSkeleton()
        }
        is SearchUiState.Discovery -> SearchDiscovery(
            state = state,
            query = query,
            onQueryChange = viewModel::onQueryChanged,
            onSubmit = { viewModel.onSearchSubmit(query) },
            showHelpIcon = showHelpIcon,
            onHelpClick = onHelpClick,
            onClearSearch = viewModel::onClearSearch,
            onRecentSearchClick = { term -> viewModel.onSearchSubmit(term) },
            onClearRecentSearches = viewModel::clearRecentSearches,
            onTimelineMonthClick = viewModel::onTimelineMonthClick,
            onCategoryClick = viewModel::onCategoryClick,
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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.items,
                        key = { it.post.id },
                    ) { feedItem ->
                        val isSaved = feedItem.post.id in state.savedPostIds
                        ArticleListItem(
                            feedItem = feedItem,
                            isSaved = isSaved,
                            onArticleClick = { onArticleClick(feedItem.post.id) },
                            onBookmarkClick = { viewModel.toggleSave(feedItem.post.id, isSaved) },
                            bookmarkContentDescription = stringResource(
                                if (isSaved) R.string.search_unsave_content_desc
                                else R.string.search_save_content_desc,
                            ),
                            readTime = feedItem.readTime,
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
