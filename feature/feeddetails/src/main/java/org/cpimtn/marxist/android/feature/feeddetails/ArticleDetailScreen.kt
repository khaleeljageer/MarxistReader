package org.cpimtn.marxist.android.feature.feeddetails

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.text.HtmlCompat
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun ArticleDetailTopBarActions(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onShareClick: (() -> Unit)?,
) {
    val ext = MarxistReaderTheme.colors
    val bookmarkTint by animateColorAsState(
        targetValue = if (isSaved) ext.bookmarkActive else ext.navInactiveIcon,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bookmark_tint",
    )
    IconButton(onClick = onSaveClick) {
        Icon(
            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = stringResource(
                if (isSaved) R.string.feeddetails_unsave_content_desc
                else R.string.feeddetails_save_content_desc,
            ),
            tint = bookmarkTint,
        )
    }
    if (onShareClick != null) {
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(R.string.feeddetails_share_content_desc),
                tint = ext.navInactiveIcon,
            )
        }
    }
}

@Composable
fun ArticleDetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.feeddetails_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ArticleDetailNotFound(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.feeddetails_not_found),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ArticleDetailError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
fun ArticleDetailContent(
    feedItem: FeedItem,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val post = feedItem.post

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Category pill — red background, white text (match reference)
        if (feedItem.categoryLabel.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ext.categoryBadgeBg,
            ) {
                Text(
                    text = feedItem.categoryLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = ext.categoryBadgeText,
                    modifier = Modifier.padding(
                        horizontal = 6.dp,
                        vertical = 4.dp,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Article title — large, bold
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Publication date — clock icon + secondary text
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
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Article body (content) — paragraph style
        HtmlText(
            content = post.content,
            color = ext.detailBody
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp), // Gap between tags horizontally
            verticalArrangement = Arrangement.spacedBy(8.dp)    // Gap between rows
        ) {
            feedItem.tagLabels.forEach { tagName ->
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

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun HtmlText(content: String, color: Color) {
    val annotatedText = remember(content) {
        val spanned = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        spanned.toAnnotatedString()
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
    )
}

fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())

    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)

        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                    start, end
                )
            }
            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline), start, end
            )
            is StrikethroughSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.LineThrough), start, end
            )
            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)), start, end
            )
            is BackgroundColorSpan -> addStyle(
                SpanStyle(background = Color(span.backgroundColor)), start, end
            )
            is RelativeSizeSpan -> {
                addStyle(
                    SpanStyle(fontSize = span.sizeChange.em), start, end
                )
            }
            is URLSpan -> {
                addStringAnnotation("URL", span.url ?: "", start, end)
                addStyle(
                    SpanStyle(
                        color = Color(0xFF1565C0),
                        textDecoration = TextDecoration.Underline
                    ),
                    start, end
                )
            }
        }
    }
}
