package org.cpimtn.marxist.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            if (windowSizeClass.widthSizeClass == androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        MarxistReaderNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}