package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.RecentSearchRepository

class AddRecentSearchUseCase(
    private val recentSearchRepository: RecentSearchRepository,
) {
    suspend operator fun invoke(query: String, maxSize: Int = 10) {
        recentSearchRepository.addRecentSearch(query.trim(), maxSize = maxSize)
    }
}
