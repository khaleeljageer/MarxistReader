package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SettingsRepository

/** Counts an article having been opened, feeding [ShouldShowReviewPromptUseCase]. */
class RecordArticleReadUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() = settingsRepository.incrementArticleReadCount()
}
