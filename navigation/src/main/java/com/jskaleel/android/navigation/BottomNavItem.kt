package com.jskaleel.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: Int, val icon: ImageVector) {
    object Feed : BottomNavItem(route = Screen.Feed.route, R.string.feed, Icons.AutoMirrored.Default.Feed)
    object Books : BottomNavItem(route = Screen.Books.route, R.string.books, Icons.Default.Book)
    object More : BottomNavItem(route = Screen.More.route, R.string.more, Icons.Default.MoreHoriz)
}