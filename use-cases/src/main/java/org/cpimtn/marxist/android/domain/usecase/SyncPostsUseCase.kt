package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.PostRepository

/**
 * Use case: trigger a full sync of posts from the server into local storage.
 * Single responsibility: orchestrate sync for background workers / manual refresh.
 */
class SyncPostsUseCase(
    private val postRepository: PostRepository,
) {
    suspend operator fun invoke(perPage: Int = DEFAULT_PER_PAGE): SyncResult =
        postRepository.fullSync(perPage)

    companion object {
        const val DEFAULT_PER_PAGE = 50
    }
}
