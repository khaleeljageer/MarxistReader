package org.cpimtn.marxist.android.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.feature.feed.R
import org.cpimtn.marxist.navigation.MainAppState
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FeedScreen(
    appState: MainAppState? = null,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.feedUiState.collectAsState()
    val categoryNames by viewModel.categoryNames.collectAsState()
    val savedPostIds by viewModel.savedPostIds.collectAsState()

    DisposableEffect(appState) {
        appState?.feedRefreshCallback = { viewModel.refresh() }
        onDispose { appState?.feedRefreshCallback = null }
    }

    when (val state = uiState) {
        is FeedUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is FeedUiState.Success -> {
            FeedContent(
                posts = state.posts,
                categoryNames = categoryNames,
                savedPostIds = savedPostIds,
                onSaveClick = { viewModel.toggleSave(it) },
            )
        }
        is FeedUiState.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.feed_empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.feed_retry))
                    }
                }
            }
        }
        is FeedUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.feed_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedContent(
    posts: List<Post>,
    categoryNames: Map<Int, String>,
    savedPostIds: Set<Int>,
    onSaveClick: (Int) -> Unit,
) {
    val ext = MarxistReaderTheme.colors
    val specialArticle = posts.firstOrNull()
    val recentPosts = if (posts.size > 1) posts.drop(1) else emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── சிறப்புக் கட்டுரை (Special Article) ──
        item(key = "header_special") {
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = ext.featuredLabel
                )
                Text(
                    text = stringResource(R.string.feed_special_article),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        if (specialArticle != null) {
            item(key = "special_${specialArticle.id}") {
                SpecialArticleCard(
                    post = specialArticle,
                    categoryNames = categoryNames,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

        // ── சமீபத்திய கட்டுரைகள் (Recent Articles) ──
        item(key = "header_recent") {
            Text(
                text = stringResource(R.string.feed_recent_articles),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        itemsIndexed(
            items = recentPosts,
            key = { _, post -> post.id }
        ) { index, post ->
            RecentArticleItem(
                post = post,
                categoryNames = categoryNames,
                isSaved = post.id in savedPostIds,
                onSaveClick = { onSaveClick(post.id) },
                modifier = Modifier.padding(vertical = 12.dp)
            )
            if (index < recentPosts.size - 1) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SpecialArticleCard(
    post: Post,
    categoryNames: Map<Int, String>,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val categoryLabel = post.categories.firstOrNull()?.let { categoryNames[it] ?: "" } ?: ""

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = ext.featuredGradientStart,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = ext.featuredTitle,
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (categoryLabel.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ext.featuredCategoryBg,
                    ) {
                        Text(
                            text = categoryLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.featuredCategoryText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = formatFeedDate(post.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.featuredDate,
                )
            }
        }
    }
}

@Composable
private fun RecentArticleItem(
    post: Post,
    categoryNames: Map<Int, String>,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val categoryLabel = post.categories.firstOrNull()?.let { categoryNames[it] ?: "" } ?: ""

    Column(modifier = modifier.fillMaxWidth()) {
        if (categoryLabel.isNotEmpty()) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = ext.categoryBadgeText,
            )
        }
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (post.excerpt.isNotBlank()) {
            Text(
                text = post.excerpt.replace(Regex("<[^>]+>"), "").trim(),
                style = MaterialTheme.typography.bodyMedium,
                color = ext.articleExcerpt,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = ext.articleTimestamp
                )
                Text(
                    text = formatFeedDate(post.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.articleTimestamp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = stringResource(if (isSaved) R.string.feed_unsave_content_desc else R.string.feed_save_content_desc),
                    tint = if (isSaved) ext.bookmarkActive else ext.bookmarkInactive,
                )
            }
        }
    }
}

private fun formatFeedDate(dateStr: String): String {
    val raw = dateStr.trim().take(19)
    return try {
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val out = SimpleDateFormat("MMM d", Locale.getDefault())
        val parsed = iso.parse(raw) ?: return dateStr.take(10)
        out.format(parsed)
    } catch (_: Exception) {
        dateStr.take(10)
    }
}
