package com.jskaleel.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.cpimtn.marxist.android.feature.books.BooksScreen
import org.cpimtn.marxist.android.feature.feed.FeedScreen
import org.cpimtn.marxist.android.feature.more.MoreScreen

@Composable
fun MainScreensNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
        modifier = modifier.then(Modifier.padding(16.dp)),
    ) {
        composable(route = Screen.Feed.route) {
            FeedScreen()
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
            MoreScreen()
        }
    }
}