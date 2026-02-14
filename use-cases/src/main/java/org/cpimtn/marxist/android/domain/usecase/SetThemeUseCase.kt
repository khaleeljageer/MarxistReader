package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class SetThemeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(theme: Theme) = settingsRepository.setTheme(theme)
}
