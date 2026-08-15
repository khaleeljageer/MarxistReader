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
    onBookClick: (bookId: String) -> Unit,
    viewModel: BooksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        // Animates on a change of state *kind* only — contentKey keeps a data-only change (a book
        // finishing its download, or being deleted) from re-running the fade. The content reads
        // the state handed to it rather than closing over `uiState`: with the outer value captured
        // instead, an update that leaves stateKey untouched left the grid showing the old
        // download states until something else forced a recomposition.
        AnimatedContent(
            targetState = uiState,
            contentKey = { it.stateKey },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "books_state",
        ) { state ->
            when (state) {
                is BooksUiState.Loading -> BooksLoadingSkeleton()
                is BooksUiState.Success -> {
                    BooksContent(
                        books = state.books,
                        downloadStates = state.downloadStates,
                        onDownloadClick = { viewModel.downloadBook(it) },
                        onDeleteClick = { viewModel.deleteBook(it) },
                        onOpenClick = { onBookClick(it.id) },
                    )
                }

                is BooksUiState.Empty -> {
                    StatusMessage(
                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                        title = stringResource(R.string.books_empty_message),
                        subtitle = stringResource(R.string.books_empty_subtitle),
                        actionLabel = stringResource(R.string.books_retry),
                        onAction = { viewModel.refresh() },
                    )
                }

                is BooksUiState.Error -> {
                    StatusMessage(
                        icon = Icons.Outlined.CloudOff,
                        title = stringResource(R.string.books_error_title),
                        subtitle = state.message,
                        actionLabel = stringResource(R.string.books_retry),
                        onAction = { viewModel.refresh() },
                    )
                }
            }
        }
    }
}