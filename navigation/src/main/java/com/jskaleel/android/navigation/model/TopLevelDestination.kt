package com.jskaleel.android.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.outlined.Feed
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.jskaleel.android.navigation.R

enum class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val iconTextId: Int,
) {
    FEED(
        route = Screen.Main.Feed.route,
        selectedIcon = Icons.AutoMirrored.Filled.Feed,
        unSelectedIcon = Icons.AutoMirrored.Outlined.Feed,
        iconTextId = R.string.feed,
    ),
    BOOKS(
        route = Screen.Main.Books.route,
        selectedIcon = Icons.Filled.Book,
        unSelectedIcon = Icons.Outlined.Book,
        iconTextId = R.string.books
    ),
    MORE(
        route = Screen.Main.More.route,
        selectedIcon = Icons.Filled.MoreHoriz,
        unSelectedIcon = Icons.Outlined.MoreHoriz,
        iconTextId = R.string.more,
    )
}