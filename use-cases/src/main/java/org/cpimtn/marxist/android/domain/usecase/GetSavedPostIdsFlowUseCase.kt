package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.repository.SavedPostRepository

class GetSavedPostIdsFlowUseCase(
    private val savedPostRepository: SavedPostRepository,
) {
    operator fun invoke(): Flow<Set<Int>> = savedPostRepository.getSavedPostIds()
}
