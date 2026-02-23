package org.cpimtn.marxist.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(val name: String) {
    data object Root : Route(name = "root")
    data object Welcome : Route(name = "welcome")
    data object Main : Route(name = "main")

    data object ArticleDetail : Route(name = "article/{postId}") {
        fun createRoute(postId: Int): String = "article/$postId"

        val arguments = listOf(
            navArgument("postId") { type = NavType.IntType }
        )
    }
}

sealed class Screen(val route: String) {
    object Feed : Screen(route = "feed")
    object Books : Screen(route = "books")
    object Search : Screen(route = "search")
    object Saved : Screen(route = "saved")
    object Settings : Screen(route = "settings")
}