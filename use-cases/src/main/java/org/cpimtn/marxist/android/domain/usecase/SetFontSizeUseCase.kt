package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class SetFontSizeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(fontSize: FontSize) = settingsRepository.setFontSize(fontSize)
}
