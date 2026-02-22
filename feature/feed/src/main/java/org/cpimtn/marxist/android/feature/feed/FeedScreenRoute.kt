package org.cpimtn.marxist.android.feature.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.navigation.MainAppState

@Composable
fun FeedScreenRoute(
    appState: MainAppState? = null,
    onArticleClick: (postId: Int) -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.feedUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    DisposableEffect(appState) {
        appState?.feedRefreshCallback = { viewModel.refresh() }
        onDispose { appState?.feedRefreshCallback = null }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedContent(
            targetState = uiState.stateKey,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "feed_state",
        ) { key ->
            when (key) {
                StateKey.Loading -> FeedLoadingSkeleton()
                StateKey.Content -> {
                    val state = uiState as? FeedUiState.Success ?: return@AnimatedContent
                    FeedContent(
                        feedItems = state.feedItems,
                        savedPostIds = state.savedPostIds,
                        onArticleClick = onArticleClick,
                        onSaveClick = { viewModel.toggleSave(it) },
                    )
                }

                StateKey.Empty -> {
                    FeedStatusMessage(
                        icon = Icons.Outlined.Inbox,
                        title = stringResource(R.string.feed_empty_message),
                        subtitle = stringResource(R.string.feed_empty_subtitle),
                        actionLabel = stringResource(R.string.feed_retry),
                        onAction = { viewModel.refresh() },
                    )
                }

                StateKey.Error -> {
                    val message = (uiState as? FeedUiState.Error)?.message ?: ""
                    FeedStatusMessage(
                        icon = Icons.Outlined.CloudOff,
                        title = stringResource(R.string.feed_error_title),
                        subtitle = message,
                        actionLabel = stringResource(R.string.feed_retry),
                        onAction = { viewModel.refresh() },
                    )
                }
            }
        }
    }
}