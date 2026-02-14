package org.cpimtn.marxist.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import org.cpimtn.marxist.android.app.App
import org.cpimtn.marxist.android.app.AppViewModel
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (settings.theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.DEFAULT -> systemDark
            }
            val windowSizeClass = calculateWindowSizeClass(this)

            MarxistReaderTheme(darkTheme = darkTheme) {
                App(
                    windowSizeClass = windowSizeClass,
                    darkTheme = darkTheme
                )
            }
        }
    }
}

