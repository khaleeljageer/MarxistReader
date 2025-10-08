package org.cpimtn.marxist.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jskaleel.android.ui.theme.MarxistReaderTheme

@Composable
fun MainScreen(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
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
