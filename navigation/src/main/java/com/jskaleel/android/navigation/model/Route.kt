package com.jskaleel.android.navigation.model

sealed class Route(val name: String) {
    data object Welcome : Route(name = "Route_Welcome")
    data object Main : Route(name = "Route_Main")
}