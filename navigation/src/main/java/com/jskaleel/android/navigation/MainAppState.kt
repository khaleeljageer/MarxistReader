package com.jskaleel.android.navigation

import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions

@Composable
fun rememberMainAppState(
    widthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
): MainAppState {

    return remember(navController, widthSizeClass) {
        MainAppState(navController, widthSizeClass)
    }
}

@Stable
class MainAppState(
    val navController: NavHostController,
    private val widthSizeClass: WindowWidthSizeClass,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            "feed" -> TopLevelDestination.FEED
            "books" -> TopLevelDestination.BOOKS
            "more" -> TopLevelDestination.MORE
            else -> null
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    val navigationSuiteType: NavigationSuiteType
        @Composable get() {
            return when (widthSizeClass) {
                WindowWidthSizeClass.Expanded -> {
                    NavigationSuiteType.NavigationDrawer
                }

                WindowWidthSizeClass.Medium -> {
                    NavigationSuiteType.NavigationRail
                }

                else -> {
                    NavigationSuiteType.NavigationBar
                }
            }
        }

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param destination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (destination) {
            TopLevelDestination.FEED -> {

            }

            TopLevelDestination.BOOKS -> {

            }

            TopLevelDestination.MORE -> {

            }
        }
    }
}