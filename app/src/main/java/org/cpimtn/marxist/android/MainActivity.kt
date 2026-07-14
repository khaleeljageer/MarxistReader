package org.cpimtn.marxist.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import org.cpimtn.marxist.android.app.App
import org.cpimtn.marxist.android.app.AppViewModel
import org.cpimtn.marxist.android.app.LocaleController
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme
import org.cpimtn.marxist.navigation.Screen

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels()

    // Result is fire-and-forget: notifications are opt-in, so a denial just means no pushes are shown.
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    // Tab requested by a notification tap (e.g. "new book" → Books), consumed once by App and cleared.
    // Held as Compose state so both a cold-start intent and onNewIntent re-trigger navigation.
    private var startTab by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startTab = intent?.getStringExtra(EXTRA_START_TAB)
        requestNotificationPermissionIfNeeded()
        setContent {
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.language) {
                LocaleController.apply(settings.language)
            }
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
                    darkTheme = darkTheme,
                    startTab = startTab,
                    onStartTabHandled = { startTab = null },
                )
            }
        }
    }

    /**
     * The launcher uses the default `standard` launch mode, but the notification's
     * FLAG_ACTIVITY_CLEAR_TOP | SINGLE_TOP reuses a running instance and delivers the tap here instead
     * of recreating the Activity. Refresh the requested tab so App re-navigates.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        startTab = intent.getStringExtra(EXTRA_START_TAB)
    }

    /**
     * POST_NOTIFICATIONS is a runtime permission only from Android 13 (Tiramisu); on older versions it
     * is granted at install time. We ask once on launch when it isn't already granted — the system shows
     * the dialog at most twice, after which the request is silently ignored, so no rationale UI is needed.
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        /** Intent extra naming the top-level tab to select on launch; value is a [Screen] route. */
        const val EXTRA_START_TAB = "start_tab"
        val TAB_BOOKS: String = Screen.Books.route
    }
}

