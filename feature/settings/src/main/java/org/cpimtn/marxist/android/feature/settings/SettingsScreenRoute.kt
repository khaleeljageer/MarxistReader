package org.cpimtn.marxist.android.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreenRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    val themeDialog = rememberSaveable { mutableStateOf(false) }
    val fontSizeDialog = rememberSaveable { mutableStateOf(false) }
    val languageDialog = rememberSaveable { mutableStateOf(false) }

    if (themeDialog.value) {
        ThemeDialog(
            current = settings.theme,
            onSelect = { viewModel.setTheme(it); themeDialog.value = false },
            onDismiss = { themeDialog.value = false },
        )
    }
    if (fontSizeDialog.value) {
        FontSizeDialog(
            current = settings.fontSize,
            onSelect = { viewModel.setFontSize(it); fontSizeDialog.value = false },
            onDismiss = { fontSizeDialog.value = false },
        )
    }
    if (languageDialog.value) {
        LanguageDialog(
            current = settings.language,
            onSelect = { viewModel.setLanguage(it); languageDialog.value = false },
            onDismiss = { languageDialog.value = false },
        )
    }

    SettingsScreenContent(
        uiState = SettingsUiState(
            theme = settings.theme,
            fontSize = settings.fontSize,
            language = settings.language,
            pushNotificationsEnabled = settings.pushNotificationsEnabled,
            appVersion = settings.appVersion,
        ),
        themeDialogUpdate = { themeDialog.value = it },
        fontSizeDialogUpdate = { fontSizeDialog.value = it },
        languageDialogUpdate = { languageDialog.value = it },
        onPushNotificationStatusChange = { viewModel.setPushNotificationsEnabled(it) },
    )
}