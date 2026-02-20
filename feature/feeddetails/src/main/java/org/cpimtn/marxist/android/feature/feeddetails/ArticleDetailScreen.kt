package org.cpimtn.marxist.android.feature.feeddetails

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    onBackClick: () -> Unit,
    onShareClick: (() -> Unit)? = null,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext = MarxistReaderTheme.colors

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.feeddetails_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = ext.appBarTitle,
                        fontWeight = FontWeight.Medium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.feeddetails_back),
                            tint = ext.appBarActionIcon,
                        )
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is ArticleDetailUiState.Success -> {
                            ArticleDetailTopBarActions(
                                isSaved = state.isSaved,
                                onSaveClick = { viewModel.toggleSave() },
                                onShareClick = onShareClick,
                            )
                        }

                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ext.appBarBackground,
                    titleContentColor = ext.appBarTitle,
                    navigationIconContentColor = ext.appBarActionIcon,
                    actionIconContentColor = ext.appBarActionIcon,
                ),
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
    ) { paddingValues ->
        when (val state = uiState) {
            is ArticleDetailUiState.Loading -> ArticleDetailLoading(
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.Success -> ArticleDetailContent(
                feedItem = state.feedItem,
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.NotFound -> ArticleDetailNotFound(
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.Error -> ArticleDetailError(
                message = state.message,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ArticleDetailTopBarActions(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onShareClick: (() -> Unit)?,
) {
    val ext = MarxistReaderTheme.colors
    val bookmarkTint by animateColorAsState(
        targetValue = if (isSaved) ext.bookmarkActive else ext.bookmarkInactive,
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
                tint = ext.appBarActionIcon,
            )
        }
    }
}

@Composable
private fun ArticleDetailLoading(modifier: Modifier = Modifier) {
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
private fun ArticleDetailNotFound(modifier: Modifier = Modifier) {
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
private fun ArticleDetailError(
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
private fun ArticleDetailContent(
    feedItem: FeedItem,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    val post = feedItem.post

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Category pill — red background, white text (match reference)
        if (feedItem.categoryLabel.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ext.partyRedAccent,
            ) {
                Text(
                    text = feedItem.categoryLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp,
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
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = ext.detailDate,
            )
            Text(
                text = post.formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = ext.detailDate,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Article body (content) — paragraph style
        HtmlText(
            content = post.content,
            color = ext.detailBody
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun HtmlText(content: String, color: Color) {
    val annotatedText = remember(content) {
        val spanned = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val text = spanned.toString()
        buildAnnotatedString {
            append(text)
        }
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
    )
}
