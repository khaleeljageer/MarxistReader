package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.cpimtn.marxist.android.domain.repository.SettingsRepository

/**
 * Emits true once the user has read enough articles to be worth asking for a review, and the
 * prompt has not already been offered. [minArticlesRead] is supplied by the DI module since
 * `:use-cases` has no access to app configuration.
 */
class ShouldShowReviewPromptUseCase(
    private val settingsRepository: SettingsRepository,
    private val minArticlesRead: Int,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        settingsRepository.getArticleReadCount(),
        settingsRepository.getReviewPromptShown(),
    ) { count, alreadyShown ->
        !alreadyShown && count >= minArticlesRead
    }
}
