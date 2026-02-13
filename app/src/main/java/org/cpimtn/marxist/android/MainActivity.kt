package org.cpimtn.marxist.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import org.cpimtn.marxist.android.app.App
import dagger.hilt.android.AndroidEntryPoint
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MarxistReaderTheme {
                App(windowSizeClass = windowSizeClass)
            }
        }
    }
}

