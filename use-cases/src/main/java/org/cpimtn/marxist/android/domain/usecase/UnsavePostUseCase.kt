package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SavedPostRepository

class UnsavePostUseCase(
    private val savedPostRepository: SavedPostRepository,
) {
    suspend operator fun invoke(postId: Int) = savedPostRepository.unsave(postId)
}
