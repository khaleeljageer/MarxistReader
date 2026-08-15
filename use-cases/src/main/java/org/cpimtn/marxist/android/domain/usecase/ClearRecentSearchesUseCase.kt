package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.RecentSearchRepository

class ClearRecentSearchesUseCase(
    private val recentSearchRepository: RecentSearchRepository,
) {
    suspend operator fun invoke() {
        recentSearchRepository.clearRecentSearches()
    }
}
