package org.cpimtn.marxist.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

/**
 * Top app bar with the Marxist logo (always visible) and
 * an optional search icon on applicable pages.
 *
 * Layout:
 *   [Logo image]                          [Search 🔍]
 *   ─── accent gradient line ─────────────────────────
 */
@Composable
fun MarxistTopAppBar(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSearchIcon: Boolean = true,
    isDarkMode: Boolean = isSystemInDarkTheme(),
) {
    val ext = MarxistReaderTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ext.appBarBackground)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Logo ──
            Image(
                painter = if (isDarkMode) {
                    painterResource(R.drawable.logo_marxist_dark)
                } else {
                    painterResource(R.drawable.logo_marxist_light)
                },
                contentDescription = stringResource(R.string.app_bar_title),
                modifier = Modifier.height(36.dp),
                contentScale = ContentScale.FillHeight,
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Search action (or same-size placeholder to keep bar height consistent) ──
            if (showSearchIcon) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search_content_desc),
                        tint = ext.navInactiveIcon,
                    )
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        }

        // ── Accent gradient line (party identity nod) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ext.accentLineStart, ext.accentLineEnd),
                        start = Offset.Zero,
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                    ),
                ),
        )
    }
}
