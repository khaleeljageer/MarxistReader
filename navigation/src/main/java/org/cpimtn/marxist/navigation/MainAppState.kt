package org.cpimtn.marxist.navigation

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
    /** Set by FeedScreen when composed; cleared on dispose. Used for top bar refresh (feed only). */
    var feedRefreshCallback: (() -> Unit)? = null
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            Screen.Feed.route -> TopLevelDestination.FEED
            Screen.Books.route -> TopLevelDestination.BOOKS
            Screen.Search.route -> TopLevelDestination.SEARCH
            Screen.Saved.route -> TopLevelDestination.SAVED
            Screen.Settings.route -> TopLevelDestination.SETTINGS
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
     * navigate to and from it. If the user taps the already-selected tab, we do nothing to avoid
     * recreating the current screen.
     *
     * @param destination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val targetRoute = when (destination) {
            TopLevelDestination.FEED -> Screen.Feed.route
            TopLevelDestination.BOOKS -> Screen.Books.route
            TopLevelDestination.SEARCH -> Screen.Search.route
            TopLevelDestination.SAVED -> Screen.Saved.route
            TopLevelDestination.SETTINGS -> Screen.Settings.route
        }
        if (currentRoute == targetRoute) return

        when (destination) {
            TopLevelDestination.FEED -> {
                val topLevelNavOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navController.navigate(Screen.Feed.route, topLevelNavOptions)
            }

            TopLevelDestination.BOOKS -> {
                val navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                navController.navigate(Screen.Books.route, navOptions)
            }

            TopLevelDestination.SEARCH -> {
                val navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                navController.navigate(Screen.Search.route, navOptions)
            }

            TopLevelDestination.SAVED -> {
                val navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                navController.navigate(Screen.Saved.route, navOptions)
            }

            TopLevelDestination.SETTINGS -> {
                val navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                navController.navigate(Screen.Settings.route, navOptions)
            }
        }
    }
}