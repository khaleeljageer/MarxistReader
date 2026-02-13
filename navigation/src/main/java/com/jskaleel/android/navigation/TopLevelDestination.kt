package com.jskaleel.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Feed
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val icon: ImageVector,
    val iconTextId: Int,
) {
    FEED(
        icon = Icons.AutoMirrored.Outlined.Feed,
        iconTextId = R.string.feed,
    ),
    BOOKS(
        icon = Icons.Outlined.Book,
        iconTextId = R.string.books
    ),
    MORE(
        icon = Icons.Outlined.MoreHoriz,
        iconTextId = R.string.more,
    )
}
