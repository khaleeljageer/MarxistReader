package org.cpimtn.marxist.android.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.cpimtn.marxist.android.feature.books.BooksScreenRoute
import org.cpimtn.marxist.android.feature.feed.FeedScreenRoute
import org.cpimtn.marxist.android.feature.saved.SavedScreen
import org.cpimtn.marxist.android.feature.search.SearchScreenRoute
import org.cpimtn.marxist.android.feature.settings.SettingsScreenRoute
import org.cpimtn.marxist.navigation.MainAppState
import org.cpimtn.marxist.navigation.Screen

/**
 * NavHost for main tab content. Lives in app so navigation module stays free of feature deps.
 */
@Composable
fun MainScreensNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    goToArticleDetails: (postId: Int) -> Unit,
    openBook:(bookId: Int) -> Unit,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        modifier = modifier,
    ) {
        composable(route = Screen.Feed.route) {
            FeedScreenRoute(
                appState = appState,
                onArticleClick = { postId ->
                    goToArticleDetails(postId)
                }
            )
        }
        composable(route = Screen.Books.route) {
            BooksScreenRoute(
                onBookClick = {
                    openBook(it)
                },
            )
        }
        composable(route = Screen.Search.route) {
            SearchScreenRoute(onArticleClick = goToArticleDetails)
        }
        composable(route = Screen.Saved.route) {
            SavedScreen(
                onArticleClick = goToArticleDetails,
                onTakeMeClick = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreenRoute()
        }
    }
}
