package com.jskaleel.android.navigation.model
sealed class Screen(val route: String) {
    object Welcome : Screen(route = "screen_welcome")

    object Main {
        data object Feed : Screen(route = "screen_main_feed")
        data object Books : Screen(route = "screen_main_books")
        data object More : Screen(route = "screen_main_more")
    }


    data object Search : Screen(route = "screen_search")
    data object About : Screen(route = "screen_about")
    data object Donate : Screen(route = "screen_donate")
    data object ThemeSettings : Screen(route = "screen_theme_settings")
    object FeedDetails : Screen(route = "screen_feed_details") {
        private const val FEED_ID = "feedId"
        val link = "$route/{$FEED_ID}"

        fun create(feedId: String): String {
            return "$route/$feedId"
        }

        fun get(bundle: android.os.Bundle?): String {
            return bundle?.getString(FEED_ID) ?: ""
        }
    }
}