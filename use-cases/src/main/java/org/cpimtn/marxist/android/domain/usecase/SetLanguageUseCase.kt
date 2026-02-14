package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class SetLanguageUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(language: AppLanguage) = settingsRepository.setLanguage(language)
}
