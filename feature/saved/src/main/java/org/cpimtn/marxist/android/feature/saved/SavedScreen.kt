package org.cpimtn.marxist.android.feature.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.ui.common.ArticleListItem
import org.cpimtn.marxist.android.ui.common.ArticleSkeleton
import org.cpimtn.marxist.android.ui.common.StatusMessage
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource

@Composable
fun SavedScreen(
    onArticleClick: (postId: Int) -> Unit,
    onTakeMeClick: () -> Unit,
    viewModel: SavedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.feedUiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = uiState.stateKey,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "saved_feed_state",
        ) { key ->
            when (key) {
                StateKey.Loading -> FeedLoadingSkeleton()
                StateKey.Content -> {
                    val state = uiState as? SavedFeedUiState.Success ?: return@AnimatedContent
                    SavedFeedContent(
                        feedItems = state.feedItems,
                        onArticleClick = onArticleClick,
                        onUnSaveClick = { viewModel.unsave(it) },
                    )
                }

                StateKey.Empty -> {
                    StatusMessage(
                        icon = Icons.Outlined.Inbox,
                        title = stringResource(R.string.saved_feed_empty_message),
                        subtitle = stringResource(R.string.saved_feed_empty_subtitle),
                        actionLabel = stringResource(R.string.saved_take_me),
                        onAction = onTakeMeClick,
                    )
                }

                StateKey.Error -> {
                    val message = (uiState as? SavedFeedUiState.Error)?.message ?: ""
                    StatusMessage(
                        icon = Icons.Outlined.CloudOff,
                        title = stringResource(R.string.saved_feed_error_title),
                        subtitle = message,
                        actionLabel = stringResource(R.string.saved_take_me),
                        onAction = onTakeMeClick,
                    )
                }
            }
        }
    }
}

@Composable
fun SavedFeedContent(
    feedItems: List<FeedItem>,
    onArticleClick: (Int) -> Unit,
    onUnSaveClick: (Int) -> Unit
) {
    val ext = MarxistReaderTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item(key = "saved_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.saved_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = ext.accentLineStart,
                    thickness = 1.dp,
                )
            }
        }
        itemsIndexed(
            items = feedItems,
            key = { _, item -> item.post.id }
        ) { index, feedItem ->
            ArticleListItem(
                feedItem = feedItem,
                isSaved = true,
                onArticleClick = { onArticleClick(feedItem.post.id) },
                onBookmarkClick = { onUnSaveClick(feedItem.post.id) },
                bookmarkContentDescription = stringResource(R.string.saved_unsave_content_desc),
                modifier = Modifier,
            )
            if (index < feedItems.size - 1) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FeedLoadingSkeleton() {
    repeat(5) {
        ArticleSkeleton()
        if (it < 3) {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
