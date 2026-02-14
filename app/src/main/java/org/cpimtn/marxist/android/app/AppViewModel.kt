package org.cpimtn.marxist.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.cpimtn.marxist.android.domain.model.UserSettings
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    getSettingsFlowUseCase: GetSettingsFlowUseCase,
) : ViewModel() {

    val settings = getSettingsFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings(),
        )
}
