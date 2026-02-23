package org.cpimtn.marxist.android.feature.feed

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.ui.common.ArticleListItem
import org.cpimtn.marxist.android.ui.common.ArticleSkeleton
import org.cpimtn.marxist.android.ui.common.StatusMessage
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun FeedContent(
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
            ArticleListItem(
                feedItem = feedItem,
                isSaved = feedItem.post.id in savedPostIds,
                onArticleClick = { onArticleClick(feedItem.post.id) },
                onBookmarkClick = { onSaveClick(feedItem.post.id) },
                bookmarkContentDescription = stringResource(
                    if (feedItem.post.id in savedPostIds) R.string.feed_unsave_content_desc
                    else R.string.feed_save_content_desc,
                ),
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
fun FeedLoadingSkeleton() {
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
                .background(ext.shimmerBase)
                .padding(18.dp),
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
            ArticleSkeleton()
            if (it < 3) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}