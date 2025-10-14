package com.jskaleel.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.android.navigation.model.Route
import com.jskaleel.android.navigation.model.Screen
import org.cpimtn.marxist.android.feature.books.BooksScreen
import org.cpimtn.marxist.android.feature.feed.FeedDetailsScreen
import org.cpimtn.marxist.android.feature.feed.FeedScreen
import org.cpimtn.marxist.android.feature.feed.SearchScreen
import org.cpimtn.marxist.android.feature.more.AboutScreen
import org.cpimtn.marxist.android.feature.more.DonateScreen
import org.cpimtn.marxist.android.feature.more.MoreScreen
import org.cpimtn.marxist.android.feature.more.ThemeSettingsScreen


fun NavGraphBuilder.mainNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.Main.Feed.route,
        route = Route.Main.name
    ) {
        composable(route = Screen.Main.Feed.route) {
            FeedScreen(
                navigateToDetails = { feedId ->
                    navController.navigate(
                        route = Screen.FeedDetails.create(
                            feedId = feedId
                        )
                    )
                },
                navigateToSearch = {
                    navController.navigate(
                        route = Screen.Search.route
                    )
                }
            )
        }
        composable(route = Screen.Main.Books.route) {
            BooksScreen()
        }
        composable(route = Screen.Main.More.route) {
            MoreScreen(
                navigateToAbout = {
                    navController.navigate(
                        route = Screen.About.route
                    )
                },
                navigateToDonate = {
                    navController.navigate(
                        route = Screen.Donate.route
                    )
                },
                navigateToThemeSettings = {
                    navController.navigate(
                        route = Screen.ThemeSettings.route
                    )
                }
            )
        }
        composable(route = Screen.Search.route) {
            SearchScreen()
        }
        composable(route = Screen.About.route) {
            AboutScreen()
        }
        composable(route = Screen.Donate.route) {
            DonateScreen()
        }
        composable(route = Screen.ThemeSettings.route) {
            ThemeSettingsScreen()
        }
        composable(route = Screen.FeedDetails.link) {
            val feedId = Screen.FeedDetails.get(it.arguments)
            FeedDetailsScreen(feedId = feedId)
        }
    }
}

