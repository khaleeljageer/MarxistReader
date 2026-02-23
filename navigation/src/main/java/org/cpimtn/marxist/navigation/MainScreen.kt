package org.cpimtn.marxist.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    darkTheme: Boolean,
    content: @Composable (MainAppState, Modifier) -> Unit,
) {
    val appState = rememberMainAppState(widthSizeClass = windowSizeClass.widthSizeClass)
    val currentDestination = appState.currentDestination
    val topLevelDestination = appState.currentTopLevelDestination

    // ── Item colors for each navigation variant ──
    val navItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MarxistReaderTheme.colors.navActiveIcon,
            selectedTextColor = MarxistReaderTheme.colors.navActiveLabel,
            unselectedIconColor = MarxistReaderTheme.colors.navInactiveIcon,
            unselectedTextColor = MarxistReaderTheme.colors.navInactiveLabel,
            indicatorColor = Color.Transparent,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = MarxistReaderTheme.colors.navActiveIcon,
            selectedTextColor = MarxistReaderTheme.colors.navActiveLabel,
            unselectedIconColor = MarxistReaderTheme.colors.navInactiveIcon,
            unselectedTextColor = MarxistReaderTheme.colors.navInactiveLabel,
            indicatorColor = Color.Transparent,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = MarxistReaderTheme.colors.navActiveIcon,
            selectedTextColor = MarxistReaderTheme.colors.navActiveLabel,
            unselectedIconColor = MarxistReaderTheme.colors.navInactiveIcon,
            unselectedTextColor = MarxistReaderTheme.colors.navInactiveLabel,
        ),
    )

    NavigationSuiteScaffold(
        layoutType = appState.navigationSuiteType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface, // Warm surface for bottom nav
            navigationRailContainerColor = MaterialTheme.colorScheme.surface, // Consistent for rail
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surface, // Consistent for drawer
        ),
        navigationSuiteItems = {
            if (topLevelDestination != null) {
                appState.topLevelDestinations.forEach { destination ->
                    val isSelected =
                        currentDestination.isTopLevelDestinationInHierarchy(destination)
                    item(
                        selected = isSelected,
                        icon = {
                            Icon(
                                imageVector = if (isSelected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = stringResource(destination.labelResId),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.labelResId),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        colors = navItemColors,
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                MarxistTopAppBar(
                    showSearchIcon = !currentDestination.isTopLevelDestinationInHierarchy(
                        TopLevelDestination.SEARCH
                    ),
                    showRefreshIcon = currentDestination?.route == Screen.Feed.route,
                    onRefreshClick = { appState.feedRefreshCallback?.invoke() },
                    onSearchClick = { appState.navigateToTopLevelDestination(TopLevelDestination.SEARCH) },
                    darkTheme = darkTheme
                )
            },
        ) { innerPadding ->
            content(appState, Modifier.padding(innerPadding))
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Compact Screen", showBackground = true)
@Composable
fun MainScreenCompactPreview() {
    MarxistReaderTheme {
        MainScreen(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            content = { _, modifier -> Box(modifier) {} },
            darkTheme = false
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Medium Screen", showBackground = true, widthDp = 600, heightDp = 800)
@Composable
fun MainScreenMediumPreview() {
    MarxistReaderTheme {
        MainScreen(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(600.dp, 800.dp)),
            content = { _, modifier -> Box(modifier) {} },
            darkTheme = false
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Expanded Screen", showBackground = true, widthDp = 840, heightDp = 900)
@Composable
fun MainScreenExpandedPreview() {
    MarxistReaderTheme {
        MainScreen(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(840.dp, 900.dp)),
            content = { _, modifier -> Box(modifier) {} },
            darkTheme = true
        )
    }
}
