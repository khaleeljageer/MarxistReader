package org.cpimtn.marxist.android.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.cpimtn.marxist.android.feature.feeddetails.ArticleDetailRoute
import org.cpimtn.marxist.android.feature.welcome.WelcomeScreen
import org.cpimtn.marxist.navigation.MainScreen
import org.cpimtn.marxist.navigation.Route

/**
 * Root composable: root decides welcome vs main from DataStore. Welcome shown only once.
 */
@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    darkTheme: Boolean
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Root.name,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(Route.Root.name) {
            val rootViewModel: RootViewModel = hiltViewModel()
            val destination by rootViewModel.destination.collectAsState(initial = null)

            LaunchedEffect(destination) {
                destination?.let { route ->
                    navController.navigate(route) {
                        popUpTo(Route.Root.name) { inclusive = true }
                    }
                }
            }
        }

        composable(Route.Welcome.name) {
            WelcomeScreen(
                onContinueClicked = {
                    navController.navigate(Route.Main.name) {
                        popUpTo(Route.Welcome.name) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Main.name) {
            MainScreen(
                windowSizeClass = windowSizeClass,
                darkTheme = darkTheme
            ) { appState, modifier ->
                MainScreensNavHost(
                    appState = appState,
                    modifier = modifier,
                    goToArticleDetails = {
                        navController.navigate(Route.ArticleDetail.createRoute(it)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(
            route = Route.ArticleDetail.name,
            arguments = Route.ArticleDetail.arguments,
        ) {
            ArticleDetailRoute(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
