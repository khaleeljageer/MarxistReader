package org.cpimtn.marxist.android.feature.feed

import androidx.compose.runtime.Composable
import org.cpimtn.marxist.android.feature.feeddetails.ArticleDetailScreen

/**
 * Entry point for the article detail screen. Exposed so that the app NavHost can show
 * the detail screen when navigating from feed, saved, or search. Only these modules
 * should depend on [feature:feeddetails]; the app uses this destination from the feed module.
 */
@Composable
fun ArticleDetailDestination(
    onBackClick: () -> Unit,
    onShareClick: (() -> Unit)? = null,
) {
    ArticleDetailScreen(
        onBackClick = onBackClick,
        onShareClick = onShareClick,
    )
}
