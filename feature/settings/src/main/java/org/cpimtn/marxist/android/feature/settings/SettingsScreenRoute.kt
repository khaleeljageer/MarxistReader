package org.cpimtn.marxist.android.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreenRoute(
    onRateAppClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    val themeDialog = rememberSaveable { mutableStateOf(false) }
    val languageDialog = rememberSaveable { mutableStateOf(false) }
    val helpIconDialog = rememberSaveable { mutableStateOf(false) }

    if (themeDialog.value) {
        ThemeDialog(
            current = settings.theme,
            onSelect = { viewModel.setTheme(it); themeDialog.value = false },
            onDismiss = { themeDialog.value = false },
        )
    }
    if (languageDialog.value) {
        LanguageDialog(
            current = settings.language,
            onSelect = { viewModel.setLanguage(it); languageDialog.value = false },
            onDismiss = { languageDialog.value = false },
        )
    }
    if (helpIconDialog.value) {
        HelpIconDialog(
            visible = settings.helpIconVisible,
            onToggle = { viewModel.setHelpIconVisible(it) },
            onDismiss = { helpIconDialog.value = false },
        )
    }

    SettingsScreenContent(
        uiState = SettingsUiState(
            theme = settings.theme,
            language = settings.language,
            pushNotificationsEnabled = settings.pushNotificationsEnabled,
            helpIconVisible = settings.helpIconVisible,
            appVersion = settings.appVersion,
        ),
        themeDialogUpdate = { themeDialog.value = it },
        languageDialogUpdate = { languageDialog.value = it },
        helpIconDialogUpdate = { helpIconDialog.value = it },
        onPushNotificationStatusChange = { viewModel.setPushNotificationsEnabled(it) },
        onRateAppClick = onRateAppClick,
    )
}