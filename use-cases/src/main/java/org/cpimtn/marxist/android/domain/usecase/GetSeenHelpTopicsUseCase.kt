package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class GetSeenHelpTopicsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Set<String>> = settingsRepository.getSeenHelpTopics()
}
