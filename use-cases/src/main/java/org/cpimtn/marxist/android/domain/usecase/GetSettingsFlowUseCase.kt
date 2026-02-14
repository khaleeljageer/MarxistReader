package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.UserSettings
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class GetSettingsFlowUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<UserSettings> = settingsRepository.getSettings()
}
