package org.cpimtn.marxist.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.android.domain.model.UserSettings
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SetLanguageUseCase
import org.cpimtn.marxist.android.domain.usecase.SetPushNotificationsUseCase
import org.cpimtn.marxist.android.domain.usecase.SetThemeUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsFlowUseCase: GetSettingsFlowUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setPushNotificationsUseCase: SetPushNotificationsUseCase,
) : ViewModel() {
    val settingsState: StateFlow<UserSettings> = getSettingsFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings(),
        )

    fun setTheme(theme: Theme) {
        viewModelScope.launch { setThemeUseCase(theme) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { setLanguageUseCase(language) }
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { setPushNotificationsUseCase(enabled) }
    }
}
