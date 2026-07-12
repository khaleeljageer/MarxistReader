package org.cpimtn.marxist.android.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

/**
 * Shared article list row for feed and saved screens.
 *
 * @param bookmarkContentDescription Content description for the bookmark icon (e.g. "Save article" / "Unsave article").
 *   Pass from feature module via stringResource().
 */
@Composable
fun ArticleListItem(
    feedItem: FeedItem,
    isSaved: Boolean,
    onArticleClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    bookmarkContentDescription: String,
    modifier: Modifier = Modifier,
    readTime: String? = null,
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

        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (readTime != null) {
                    Text(
                        text = readTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = ext.articleTimestamp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                ArticleListItemBookmarkButton(
                    isSaved = isSaved,
                    onClick = onBookmarkClick,
                    contentDescription = bookmarkContentDescription,
                )
            }
        }
    }
}

@Composable
private fun ArticleListItemBookmarkButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
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
            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
