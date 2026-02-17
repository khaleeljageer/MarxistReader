package org.cpimtn.marxist.android.feature.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SavedScreen(
    viewModel: SavedViewModel = hiltViewModel()
) {
    val savedPosts by viewModel.savedPosts.collectAsState()
    val categoryNames by viewModel.categoryNames.collectAsState()
    val tagNames by viewModel.tagNames.collectAsState()
    val ext = MarxistReaderTheme.colors

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header: சேமித்த கட்டுரைகள் (N)
        Text(
            text = "${stringResource(R.string.saved_title)} (${savedPosts.size})",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = ext.accentLineStart,
            thickness = 2.dp
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(
                items = savedPosts,
                key = { _, post -> post.id }
            ) { index, post ->
                SavedArticleItem(
                    post = post,
                    categoryNames = categoryNames,
                    tagNames = tagNames,
                    onUnsaveClick = { viewModel.unsave(post.id) },
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                if (index < savedPosts.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedArticleItem(
    post: Post,
    categoryNames: Map<Int, String>,
    tagNames: Map<Int, String>,
    onUnsaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ext = MarxistReaderTheme.colors
    val categoryLabel = post.categories.firstOrNull()?.let { categoryNames[it] ?: "" } ?: ""
    val tagLabels = post.tags.mapNotNull { tagNames[it] }.filter { it.isNotBlank() }

    Column(modifier = modifier.fillMaxWidth()) {
        // Category and date on same row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (categoryLabel.isNotEmpty()) {
                Text(
                    text = categoryLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = ext.categoryBadgeText
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = ext.articleTimestamp
                )
                Text(
                    text = formatSavedDate(post.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.articleTimestamp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        if (post.excerpt.isNotBlank()) {
            Text(
                text = post.excerpt.replace(Regex("<[^>]+>"), "").trim(),
                style = MaterialTheme.typography.bodyMedium,
                color = ext.articleExcerpt,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        // Tags (bottom-left) and filled bookmark (bottom-right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                tagLabels.take(3).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ext.tagChipBg
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = ext.tagChipText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            IconButton(
                onClick = onUnsaveClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = stringResource(R.string.saved_unsave_content_desc),
                    tint = ext.bookmarkActive
                )
            }
        }
    }
}

private fun formatSavedDate(dateStr: String): String {
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
