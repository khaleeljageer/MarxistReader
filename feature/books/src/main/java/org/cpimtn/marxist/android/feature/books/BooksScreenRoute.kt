package org.cpimtn.marxist.android.feature.books

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.ui.common.StatusMessage

@Composable
fun BooksScreenRoute(
    onBookClick: (bookId: Int) -> Unit,
    viewModel: BooksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedContent(
            targetState = uiState.stateKey,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "books_state",
        ) { key ->
            when (key) {
                StateKey.Loading -> BooksLoadingSkeleton()
                StateKey.Content -> {
                    val state = uiState as? BooksUiState.Success ?: return@AnimatedContent
                    BooksContent(
                        books = state.books,
                        downloadStates = state.downloadStates,
                        onDownloadClick = { viewModel.downloadBook(it) },
                    )
                }

                StateKey.Empty -> {
                    StatusMessage(
                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                        title = stringResource(R.string.books_empty_message),
                        subtitle = stringResource(R.string.books_empty_subtitle),
                        actionLabel = stringResource(R.string.books_retry),
                        onAction = { viewModel.refresh() },
                    )
                }

                StateKey.Error -> {
                    val message = (uiState as? BooksUiState.Error)?.message ?: ""
                    StatusMessage(
                        icon = Icons.Outlined.CloudOff,
                        title = stringResource(R.string.books_error_title),
                        subtitle = message,
                        actionLabel = stringResource(R.string.books_retry),
                        onAction = { viewModel.refresh() },
                    )
                }
            }
        }
    }
}