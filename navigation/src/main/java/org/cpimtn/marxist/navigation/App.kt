package org.cpimtn.marxist.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Welcome.name) {
        composable(Route.Welcome.name) {
            WelcomeScreen(onContinueClicked = {
                navController.navigate(Route.Main.name) {
                    popUpTo(Route.Welcome.name) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Route.Main.name) {
            MainScreen(windowSizeClass = windowSizeClass)
        }
    }
}
