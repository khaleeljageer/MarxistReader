package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.HelpTopic
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

class MarkHelpSeenUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(topic: HelpTopic) = settingsRepository.markHelpSeen(topic.name)
}
