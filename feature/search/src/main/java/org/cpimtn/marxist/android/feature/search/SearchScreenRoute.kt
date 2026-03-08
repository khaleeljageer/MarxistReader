package org.cpimtn.marxist.android.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SearchUiState.Loading -> SearchLoadingSkeleton()
        SearchUiState.NotFound -> NoResultsMessage(query = "query")
        is SearchUiState.Discovery -> {
            SearchDiscovery()
        }
        is SearchUiState.Results -> TODO()
        is SearchUiState.Error -> SearchErrorMessage(
            message = state.message,
            onRetry = { viewModel.onSearchSubmit(query = "query") },
        )
    }
}