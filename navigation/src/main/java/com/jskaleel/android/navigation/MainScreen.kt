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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jskaleel.android.navigation.model.Route
import com.jskaleel.android.navigation.model.TopLevelDestination

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun MainScreen(
    windowSize: DpSize,
    navController: NavHostController = rememberNavController(),
    appState: MainAppState = rememberMainAppState(
        windowSize = windowSize
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
//
//    val currentDestination = navBackStackEntry?.destination
//    val currentRoute: String = currentDestination?.route ?: ""
//    val showNavigation: Boolean = remember(currentRoute) {
//        TopLevelDestination.entries.any { it.route == currentRoute }
//    }

    NavigationSuiteScaffold(
        layoutType = appState.navigationSuiteType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface, // Warm surface for bottom nav
            navigationRailContainerColor = MaterialTheme.colorScheme.surface, // Consistent for rail
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surface, // Consistent for drawer
        ),
        navigationSuiteItems = {
//            if (showNavigation) {
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
//            }
        },
    ) {
        MainAppContent(navController = navController)
    }
}

@Composable
private fun MainAppContent(navController: NavHostController) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) { NavigationHost(navController = navController) }
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


@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Preview
@Composable
fun MainScreenCompactPreview() {
    MaterialTheme {
        MainScreen(
            windowSize = DpSize.Zero,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Preview(device = "spec:width=673dp,height=841dp")
@Composable
fun MainScreenExtendedPreview() {
    MaterialTheme {
        MainScreen(
            windowSize = DpSize(width = 673.dp, height = 841.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MainScreenLargePreview() {
    MaterialTheme {
        MainScreen(
            windowSize = DpSize(width = 1280.dp, height = 800.dp),
        )
    }
}