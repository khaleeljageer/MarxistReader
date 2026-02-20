package org.cpimtn.marxist.android.feature.feeddetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailRoute(
    onBackClick: () -> Unit,
    ext: MarxistExtendedColors = MarxistReaderTheme.colors,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.feeddetails_back),
                            tint = ext.appBarActionIcon,
                        )
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is ArticleDetailUiState.Success -> {
                            ArticleDetailTopBarActions(
                                isSaved = state.isSaved,
                                onSaveClick = { viewModel.toggleSave() },
                                onShareClick = { },
                            )
                        }

                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ext.appBarBackground,
                    titleContentColor = ext.appBarTitle,
                    navigationIconContentColor = ext.appBarActionIcon,
                    actionIconContentColor = ext.appBarActionIcon,
                ),
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is ArticleDetailUiState.Loading -> ArticleDetailLoading(
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.Success -> ArticleDetailContent(
                feedItem = state.feedItem,
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.NotFound -> ArticleDetailNotFound(
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.Error -> ArticleDetailError(
                message = state.message,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}