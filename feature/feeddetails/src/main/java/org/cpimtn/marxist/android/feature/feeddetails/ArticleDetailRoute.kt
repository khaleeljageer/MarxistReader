package org.cpimtn.marxist.android.feature.feeddetails

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailRoute(
    onBackClick: () -> Unit,
    ext: MarxistExtendedColors = MarxistReaderTheme.colors,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var fontSizeDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (fontSizeDialogVisible) {
        val current = uiState
        if (current is ArticleDetailUiState.Success) {
            FontSizeDialog(
                current = current.fontSize,
                onSelect = { viewModel.setFontSize(it) },
                onDismiss = { fontSizeDialogVisible = false },
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.feed_details_back),
                            tint = ext.navInactiveIcon,
                        )
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is ArticleDetailUiState.Success -> {
                            ArticleDetailTopBarActions(
                                isSaved = state.isSaved,
                                onSaveClick = { viewModel.toggleSave() },
                                onShareClick = {
                                    context.startActivity(
                                        sharePost(
                                            title = state.feedItem.post.title,
                                            excerpt = state.feedItem.post.excerpt,
                                            url = state.feedItem.post.slug,
                                        )
                                    )
                                },
                                onFontSizeClick = { fontSizeDialogVisible = true },
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
    ) { paddingValues ->
        when (val state = uiState) {
            is ArticleDetailUiState.Loading -> ArticleDetailLoading(
                modifier = Modifier.padding(paddingValues),
            )

            is ArticleDetailUiState.Success -> ArticleDetailContent(
                feedItem = state.feedItem,
                fontSize = state.fontSize,
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

private fun sharePost(title: String, excerpt: String, url: String): Intent? {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "$title\n$excerpt\n$url")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    return shareIntent
}