package org.cpimtn.marxist.android.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.cpimtn.marxist.android.feature.books.BooksScreen
import org.cpimtn.marxist.android.feature.feed.FeedScreen
import org.cpimtn.marxist.android.feature.more.MoreScreen
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
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
        modifier = modifier,
    ) {
        composable(route = Screen.Feed.route) {
            FeedScreen(appState = appState)
        }
        composable(route = Screen.Books.route) {
            BooksScreen()
        }
        composable(route = Screen.Search.route) {
            MoreScreen()
        }
        composable(route = Screen.Saved.route) {
            MoreScreen()
        }
        composable(route = Screen.Settings.route) {
            SettingsScreenRoute()
        }
    }
}
