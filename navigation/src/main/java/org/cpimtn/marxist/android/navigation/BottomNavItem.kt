package org.cpimtn.marxist.android.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, @StringRes val title: Int, val icon: ImageVector) {
    object Feed : BottomNavItem("feed", R.string.feed, Icons.AutoMirrored.Default.Feed)
    object Books : BottomNavItem("books", R.string.books, Icons.Default.Book)
    object More : BottomNavItem("more", R.string.more, Icons.Default.MoreHoriz)
}