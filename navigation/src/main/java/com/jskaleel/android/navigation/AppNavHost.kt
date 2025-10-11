package com.jskaleel.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.cpimtn.marxist.android.feature.books.BooksScreen
import org.cpimtn.marxist.android.feature.feed.FeedScreen
import org.cpimtn.marxist.android.feature.more.MoreScreen

@Composable
fun MarxistReaderNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "feed",
        modifier = modifier,
    ) {
        composable("feed") {
            FeedScreen()
        }
        composable("books") {
            BooksScreen()
        }
        composable("more") {
            MoreScreen()
        }
    }
}