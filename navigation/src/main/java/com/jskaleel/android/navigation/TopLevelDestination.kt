package com.jskaleel.android.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top-level destinations accessible from the adaptive navigation suite
 * (bottom bar / rail / drawer depending on screen width).
 *
 * Each entry maps to a unique screen in the single-Activity architecture.
 */

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val labelResId: Int,
) {
    FEED(
        selectedIcon = Icons.Filled.RssFeed,
        unselectedIcon = Icons.Outlined.RssFeed,
        labelResId = R.string.nav_feed,
    ),
    BOOKS(
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
        labelResId = R.string.nav_books,
    ),
    SEARCH(
        selectedIcon = Icons.Rounded.Search,
        unselectedIcon = Icons.Outlined.Search,
        labelResId = R.string.nav_search,
    ),
    SAVED(
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
        labelResId = R.string.nav_saved,
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelResId = R.string.nav_settings,
    ),
}
