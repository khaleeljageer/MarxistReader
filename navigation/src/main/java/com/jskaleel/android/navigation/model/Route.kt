package com.jskaleel.android.navigation.model

sealed class Route(val name: String) {
    data object Welcome : Route(name = "Route_Welcome")
    data object Main : Route(name = "Route_Main")
    data object Feed : Route(name = "Route_Feed")
    data object Books : Route(name = "Route_Books")
    data object More : Route(name = "Route_More")
}