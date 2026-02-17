package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.SavedPostRepository

class SavePostUseCase(
    private val savedPostRepository: SavedPostRepository,
) {
    suspend operator fun invoke(postId: Int) = savedPostRepository.save(postId)
}
