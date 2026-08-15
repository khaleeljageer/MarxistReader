package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SettingsRepository

/**
 * Records that the review prompt has been offered. Play never reports whether a review was
 * actually left, so this is called once the flow has been *attempted* — success or failure — and
 * the prompt is never raised again.
 */
class MarkReviewPromptShownUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() = settingsRepository.setReviewPromptShown()
}
