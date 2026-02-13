package com.jskaleel.android.navigation

sealed class Route(val name: String) {
    data object Welcome : Route(name = "welcome")
    data object Main : Route(name = "main")
}

sealed class Screen(val route: String) {
    object Feed : Screen(route = "feed")
    object Books : Screen(route = "books")
    object More : Screen(route = "more")
}