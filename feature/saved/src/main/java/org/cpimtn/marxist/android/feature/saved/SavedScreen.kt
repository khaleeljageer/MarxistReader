package org.cpimtn.marxist.android.feature.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@Composable
fun SavedScreen(
    onArticleClick: (postId: Int) -> Unit = {},
    viewModel: SavedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.feedUiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "saved_feed_state",
        ) { state ->
            when (state) {
                is SavedFeedUiState.Loading -> FeedLoadingSkeleton()

                is SavedFeedUiState.Success -> FeedContent(
                    feedItems = state.feedItems,
                    onArticleClick = onArticleClick,
                    onUnSaveClick = { viewModel.unsave(it) },
                )

                is SavedFeedUiState.Empty -> FeedStatusMessage(
                    icon = Icons.Outlined.Inbox,
                    title = stringResource(R.string.saved_feed_empty_message),
                    subtitle = stringResource(R.string.saved_feed_empty_subtitle),
                    actionLabel = stringResource(R.string.saved_take_me),
                    onAction = { },
                )

                is SavedFeedUiState.Error -> FeedStatusMessage(
                    icon = Icons.Outlined.CloudOff,
                    title = stringResource(R.string.saved_feed_error_title),
                    subtitle = state.message,
                    actionLabel = stringResource(R.string.saved_take_me),
                    onAction = { },
                )
            }
        }
    }
}

@Composable
fun FeedContent(
    feedItems: List<FeedItem>,
    onArticleClick: (Int) -> Unit,
    onUnSaveClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(
            items = feedItems,
            key = { _, item -> item.post.id }
        ) { index, post ->
            SavedArticleItem(
                feedItem = post,
                onUnsaveClick = { onUnSaveClick(post.post.id) },
                onArticleClick = { onArticleClick(post.post.id) },
                modifier = Modifier
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
private fun FeedStatusMessage(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    val ext = MarxistReaderTheme.colors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            // Icon container
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ext.settingsIconBg,
                modifier = Modifier.size(64.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ext.settingsIconTint,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (actionLabel.isNotEmpty()) {
                Surface(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 10.dp,
                        ),
                    )
                }
            }
        }
    }
}


@Composable
private fun FeedLoadingSkeleton() {
    val ext = MarxistReaderTheme.colors
    repeat(5) {
        ArticleSkeleton(ext)
        if (it < 3) {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
private fun ArticleSkeleton(ext: MarxistExtendedColors) {
    Column {
        // Category badge
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Title line 1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Title line 2
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Excerpt
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Tags row
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ext.shimmerBase),
                )
            }
        }
    }
}

@Composable
private fun SavedArticleItem(
    feedItem: FeedItem,
    onArticleClick: () -> Unit,
    onUnsaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = MarxistReaderTheme.colors
    val post = feedItem.post
    val categoryLabel = feedItem.categoryLabel
    val tagLabels = feedItem.tagLabels

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onArticleClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Row 1: Category pill (left) + timestamp (right) — same as feed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (categoryLabel.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ext.categoryBadgeBg,
                ) {
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.categoryBadgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = ext.articleTimestamp,
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = post.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.articleTimestamp,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: Title
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        // Row 3: Excerpt
        if (post.excerpt.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.excerpt,
                style = MaterialTheme.typography.bodySmall,
                color = ext.articleExcerpt,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Row 4: Tags + Bookmark — same as feed (max 2 tags, chip with border)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (tagLabels.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    items(items = tagLabels, key = { it }) { tagName ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ext.tagChipBg,
                            border = BorderStroke(1.dp, ext.tagChipBorder),
                        ) {
                            Text(
                                text = tagName,
                                style = MaterialTheme.typography.labelSmall,
                                color = ext.tagChipText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            IconButton(
                onClick = onUnsaveClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = stringResource(R.string.saved_unsave_content_desc),
                    tint = ext.bookmarkActive,
                )
            }
        }
    }
}
