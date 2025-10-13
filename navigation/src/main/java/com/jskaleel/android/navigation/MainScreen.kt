package com.jskaleel.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jskaleel.android.navigation.model.Route
import com.jskaleel.android.navigation.model.TopLevelDestination
import com.jskaleel.android.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val navigationSuiteType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationDrawer
        WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }

    NavigationSuiteScaffold(
        layoutType = navigationSuiteType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface, // Warm surface for bottom nav
            navigationRailContainerColor = MaterialTheme.colorScheme.surface, // Consistent for rail
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surface, // Consistent for drawer
        ),
        navigationSuiteItems = {
            customNavigationSuiteItems(
                navBackStackEntry = navBackStackEntry,
                onClick = { destination ->
                    val topLevelNavOptions = navOptions {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Route.Main.name) {
                            saveState = true
                        }
                    }
                    navController.navigate(destination.route, topLevelNavOptions)
                }
            )
        },
    ) {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .fillMaxSize()
            ) {
                NavigationHost(
                    navController = navController,
                )
            }
        }
    }
}

@Stable
fun NavigationSuiteScope.customNavigationSuiteItems(
    navBackStackEntry: NavBackStackEntry?,
    onClick: (TopLevelDestination) -> Unit
) {
    val currentDestination = navBackStackEntry?.destination
    TopLevelDestination.entries.forEach { destination ->
        val isSelected =
            currentDestination.isTopLevelDestinationInHierarchy(destination)
        item(
            selected = isSelected,
            icon = {
                Icon(
                    imageVector = if (isSelected) {
                        destination.selectedIcon
                    } else {
                        destination.unSelectedIcon
                    },
                    contentDescription = stringResource(destination.iconTextId),
                )
            },
            label = {
                Text(
                    text = stringResource(destination.iconTextId),
                    fontWeight = if (isSelected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    }
                )
            },
            onClick = { onClick(destination) },
        )
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
            WindowSizeClass.calculateFromSize(
                DpSize(411.dp, 891.dp)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Medium Screen", showBackground = true, widthDp = 600, heightDp = 800)
@Composable
fun MainScreenMediumPreview() {
    MarxistReaderTheme {
        MainScreen(
            WindowSizeClass.calculateFromSize(
                DpSize(600.dp, 800.dp)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Expanded Screen", showBackground = true, widthDp = 840, heightDp = 900)
@Composable
fun MainScreenExpandedPreview() {
    MarxistReaderTheme {
        MainScreen(
            WindowSizeClass.calculateFromSize(DpSize(840.dp, 900.dp))
        )
    }
}
