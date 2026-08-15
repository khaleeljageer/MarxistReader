package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.repository.RecentSearchRepository

class GetRecentSearchesFlowUseCase(
    private val recentSearchRepository: RecentSearchRepository,
) {
    operator fun invoke(maxSize: Int = 10): Flow<List<String>> =
        recentSearchRepository.getRecentSearches(maxSize = maxSize)
}
