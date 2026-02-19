package org.cpimtn.marxist.android.feature.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.navigation.MainAppState
import org.cpimtn.marxist.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme


private val HtmlTagRegex = Regex("<[^>]+>")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    appState: MainAppState? = null,
    onArticleClick: (postId: Int) -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.feedUiState.collectAsStateWithLifecycle()
    val savedPostIds by viewModel.savedPostIds.collectAsStateWithLifecycle()
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
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "feed_state",
        ) { state ->
            when (state) {
                is FeedUiState.Loading -> FeedLoadingSkeleton()

                is FeedUiState.Success -> FeedContent(
                    feedItems = state.feedItems,
                    savedPostIds = savedPostIds,
                    onArticleClick = onArticleClick,
                    onSaveClick = { viewModel.toggleSave(it) },
                )

                is FeedUiState.Empty -> FeedStatusMessage(
                    icon = Icons.Outlined.Inbox,
                    title = stringResource(R.string.feed_empty_message),
                    subtitle = stringResource(R.string.feed_empty_subtitle),
                    actionLabel = stringResource(R.string.feed_retry),
                    onAction = { viewModel.refresh() },
                )

                is FeedUiState.Error -> FeedStatusMessage(
                    icon = Icons.Outlined.CloudOff,
                    title = stringResource(R.string.feed_error_title),
                    subtitle = state.message,
                    actionLabel = stringResource(R.string.feed_retry),
                    onAction = { viewModel.refresh() },
                )
            }
        }
    }
}

@Composable
private fun FeedContent(
    feedItems: List<FeedItem>,
    savedPostIds: Set<Int>,
    onArticleClick: (Int) -> Unit,
    onSaveClick: (Int) -> Unit,
) {
    val specialItem = feedItems.firstOrNull()
    val recentItems = remember(feedItems) {
        if (feedItems.size > 1) feedItems.drop(1) else emptyList()
    }

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = bottomPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Featured Article ──
        if (specialItem != null) {
            item(key = "featured_${specialItem.post.id}") {
                SpecialArticleCard(
                    feedItem = specialItem,
                    onClick = { onArticleClick(specialItem.post.id) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // ── Section Header ──
        if (recentItems.isNotEmpty()) {
            item(key = "header_recent") {
                SectionHeader(
                    title = stringResource(R.string.feed_recent_articles),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 4.dp,
                    ),
                )
            }
        }

        // ── Article List ──
        itemsIndexed(
            items = recentItems,
            key = { _, item -> item.post.id },
        ) { index, feedItem ->
            RecentArticleItem(
                feedItem = feedItem,
                isSaved = feedItem.post.id in savedPostIds,
                onArticleClick = { onArticleClick(feedItem.post.id) },
                onSaveClick = { onSaveClick(feedItem.post.id) },
                modifier = Modifier,
            )
            if (index < recentItems.lastIndex) {
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
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {

    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
private fun SpecialArticleCard(
    feedItem: FeedItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val post = feedItem.post
    val categoryLabel = feedItem.categoryLabel

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ext.featuredGradientStart,
                        ext.featuredGradientEnd,
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Column {
            // Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ext.featuredLabel,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.feed_special_article),
                    style = MaterialTheme.typography.labelLarge,
                    color = ext.featuredLabel,
                )
            }

            // Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = ext.featuredTitle,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            // Meta: category + date
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (categoryLabel.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ext.featuredCategoryBg,
                    ) {
                        Text(
                            text = categoryLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.featuredCategoryText,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp,
                            ),
                        )
                    }
                }
                Text(
                    text = post.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.featuredDate,
                )
            }
        }
    }
}

@Composable
private fun RecentArticleItem(
    feedItem: FeedItem,
    isSaved: Boolean,
    onArticleClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val post = feedItem.post
    val categoryLabel = feedItem.categoryLabel
    val tagLabels = feedItem.tagLabels

    // Strip HTML once, remembered across recompositions
    val cleanExcerpt = remember(post.excerpt) {
        post.excerpt.replace(HtmlTagRegex, "").trim()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onArticleClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // ── Row 1: Category + Timestamp ──
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
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 4.dp,
                        ),
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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

        // ── Row 2: Title ──
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        // ── Row 3: Excerpt ──
        if (cleanExcerpt.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cleanExcerpt,
                style = MaterialTheme.typography.bodySmall,
                color = ext.articleExcerpt,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // ── Row 4: Tags + Bookmark ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Tags — scrollable when overflow
            if (tagLabels.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    items(
                        items = tagLabels,
                        key = { it },
                    ) { tagName ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ext.tagChipBg,
                            border = BorderStroke(1.dp, ext.tagChipBorder),
                        ) {
                            Text(
                                text = tagName,
                                style = MaterialTheme.typography.labelSmall,
                                color = ext.tagChipText,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 3.dp,
                                ),
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Bookmark — animated color transition
            BookmarkButton(
                isSaved = isSaved,
                onClick = onSaveClick,
            )
        }
    }
}

@Composable
private fun BookmarkButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    val tint by animateColorAsState(
        targetValue = if (isSaved) ext.bookmarkActive else ext.bookmarkInactive,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bookmark_tint",
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Filled.Bookmark
            else Icons.Outlined.BookmarkBorder,
            contentDescription = stringResource(
                if (isSaved) R.string.feed_unsave_content_desc
                else R.string.feed_save_content_desc,
            ),
            tint = tint,
        )
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

@Composable
private fun FeedLoadingSkeleton() {
    val ext = MarxistReaderTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Featured card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ext.shimmerBase),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section header skeleton
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Article skeletons
        repeat(4) {
            ArticleSkeleton(ext)
            if (it < 3) {
                Spacer(modifier = Modifier.height(20.dp))
            }
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