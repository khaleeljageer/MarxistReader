package org.cpimtn.marxist.navigation

sealed class Route(val name: String) {
    data object Welcome : Route(name = "welcome")
    data object Main : Route(name = "main")
}

sealed class Screen(val route: String) {
    object Feed : Screen(route = "feed")
    object Books : Screen(route = "books")
    object Search : Screen(route = "search")
    object Saved : Screen(route = "saved")
    object Settings : Screen(route = "settings")
}