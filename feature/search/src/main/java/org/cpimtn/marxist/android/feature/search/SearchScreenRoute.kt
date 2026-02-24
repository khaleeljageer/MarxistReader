package org.cpimtn.marxist.android.feature.search

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchScreen(
        viewModel = viewModel
    )
}