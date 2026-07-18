package org.cpimtn.marxist.android.ui.common.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.OfflinePin
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.HelpTopic
import org.cpimtn.marxist.android.ui.common.R
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

/** A single "how to use" tip: an icon plus a short title and one-line description. */
data class HelpTip(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

/**
 * Contextual help for a screen, shown as a modal bottom sheet with a scrollable
 * list of [HelpTip]s. Auto-shown once on first visit and re-openable from a help icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpBottomSheet(
    topic: HelpTopic,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        HelpSheetContent(
            title = stringResource(helpTitleResFor(topic)),
            tips = helpTipsFor(topic),
        )
    }
}

/**
 * Renders the help title and tip rows. Kept separate from [HelpBottomSheet] so the same
 * layout can be embedded in a fragment-hosted bottom sheet (e.g. the EPUB reader).
 */
@Composable
fun HelpSheetContent(
    title: String,
    tips: List<HelpTip>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        tips.forEachIndexed { index, tip ->
            if (index > 0) Spacer(modifier = Modifier.height(18.dp))
            HelpTipRow(tip)
        }
    }
}

@Composable
private fun HelpTipRow(tip: HelpTip) {
    val ext = MarxistReaderTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ext.settingsIconBg,
            modifier = Modifier.size(44.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = tip.icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = ext.settingsIconTint,
                )
            }
        }
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun helpTitleResFor(topic: HelpTopic): Int = when (topic) {
    HelpTopic.FEED -> R.string.help_feed_title
    HelpTopic.BOOKS -> R.string.help_books_title
    HelpTopic.SEARCH -> R.string.help_search_title
    HelpTopic.SAVED -> R.string.help_saved_title
    HelpTopic.SETTINGS -> R.string.help_settings_title
    HelpTopic.ARTICLE -> R.string.help_article_title
}

@Composable
private fun helpTipsFor(topic: HelpTopic): List<HelpTip> = when (topic) {
    HelpTopic.FEED -> listOf(
        HelpTip(
            Icons.Outlined.TouchApp,
            stringResource(R.string.help_feed_read_title),
            stringResource(R.string.help_feed_read_desc),
        ),
        HelpTip(
            Icons.Outlined.BookmarkBorder,
            stringResource(R.string.help_feed_save_title),
            stringResource(R.string.help_feed_save_desc),
        ),
        HelpTip(
            Icons.Outlined.Search,
            stringResource(R.string.help_feed_search_title),
            stringResource(R.string.help_feed_search_desc),
        ),
        HelpTip(
            Icons.Outlined.Refresh,
            stringResource(R.string.help_feed_refresh_title),
            stringResource(R.string.help_feed_refresh_desc),
        ),
    )

    HelpTopic.BOOKS -> listOf(
        HelpTip(
            Icons.Outlined.Book,
            stringResource(R.string.help_books_open_title),
            stringResource(R.string.help_books_open_desc),
        ),
        HelpTip(
            Icons.Outlined.Download,
            stringResource(R.string.help_books_download_title),
            stringResource(R.string.help_books_download_desc),
        ),
        HelpTip(
            Icons.Outlined.OfflinePin,
            stringResource(R.string.help_books_offline_title),
            stringResource(R.string.help_books_offline_desc),
        ),
    )

    HelpTopic.SEARCH -> listOf(
        HelpTip(
            Icons.Outlined.Search,
            stringResource(R.string.help_search_type_title),
            stringResource(R.string.help_search_type_desc),
        ),
        HelpTip(
            Icons.Outlined.History,
            stringResource(R.string.help_search_recent_title),
            stringResource(R.string.help_search_recent_desc),
        ),
        HelpTip(
            Icons.Outlined.TouchApp,
            stringResource(R.string.help_search_open_title),
            stringResource(R.string.help_search_open_desc),
        ),
    )

    HelpTopic.SAVED -> listOf(
        HelpTip(
            Icons.Outlined.Bookmark,
            stringResource(R.string.help_saved_what_title),
            stringResource(R.string.help_saved_what_desc),
        ),
        HelpTip(
            Icons.Outlined.OfflinePin,
            stringResource(R.string.help_saved_offline_title),
            stringResource(R.string.help_saved_offline_desc),
        ),
        HelpTip(
            Icons.Outlined.BookmarkBorder,
            stringResource(R.string.help_saved_remove_title),
            stringResource(R.string.help_saved_remove_desc),
        ),
    )

    HelpTopic.SETTINGS -> listOf(
        HelpTip(
            Icons.Outlined.DarkMode,
            stringResource(R.string.help_settings_theme_title),
            stringResource(R.string.help_settings_theme_desc),
        ),
        HelpTip(
            Icons.Outlined.TextFields,
            stringResource(R.string.help_settings_text_title),
            stringResource(R.string.help_settings_text_desc),
        ),
        HelpTip(
            Icons.Outlined.Language,
            stringResource(R.string.help_settings_language_title),
            stringResource(R.string.help_settings_language_desc),
        ),
        HelpTip(
            Icons.Outlined.Notifications,
            stringResource(R.string.help_settings_notify_title),
            stringResource(R.string.help_settings_notify_desc),
        ),
    )

    HelpTopic.ARTICLE -> listOf(
        HelpTip(
            Icons.Outlined.TextFields,
            stringResource(R.string.help_article_text_title),
            stringResource(R.string.help_article_text_desc),
        ),
        HelpTip(
            Icons.Outlined.BookmarkBorder,
            stringResource(R.string.help_article_save_title),
            stringResource(R.string.help_article_save_desc),
        ),
        HelpTip(
            Icons.Outlined.Share,
            stringResource(R.string.help_article_share_title),
            stringResource(R.string.help_article_share_desc),
        ),
        HelpTip(
            Icons.Outlined.Link,
            stringResource(R.string.help_article_links_title),
            stringResource(R.string.help_article_links_desc),
        ),
    )
}
