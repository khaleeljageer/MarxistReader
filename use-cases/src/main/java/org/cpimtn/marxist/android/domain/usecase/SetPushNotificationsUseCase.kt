package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class SetPushNotificationsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = settingsRepository.setPushNotificationsEnabled(enabled)
}
