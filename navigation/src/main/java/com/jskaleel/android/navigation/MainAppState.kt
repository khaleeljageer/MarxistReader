package com.jskaleel.android.navigation

import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun rememberMainAppState(
    windowSize: DpSize,
): MainAppState {
    return remember(windowSize) {
        MainAppState(windowSize)
    }
}

@Stable
class MainAppState(
    private val windowSize: DpSize,
) {
    /**
     * Per <a href="https://m3.material.io/components/navigation-drawer/guidelines">Material Design 3 guidelines</a>,
     * the selection of the appropriate navigation component should be contingent on the available
     * window size:
     * - Bottom Bar for compact window sizes (below 600dp)
     * - Navigation Rail for medium and expanded window sizes up to 1240dp (between 600dp and 1240dp)
     * - Navigation Drawer to window size above 1240dp
     */
    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    val navigationSuiteType: NavigationSuiteType
        @Composable get() {
            return if (windowSize.width > 1240.dp) {
                NavigationSuiteType.NavigationDrawer
            } else if (windowSize.width >= 600.dp) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteType.NavigationBar
            }
        }
}