package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class SetHelpIconVisibleUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(visible: Boolean) = settingsRepository.setHelpIconVisible(visible)
}
