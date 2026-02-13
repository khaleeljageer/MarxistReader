package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.PostRepository

/**
 * Use case: observe the stream of posts from local storage (offline-first source of truth).
 * Single responsibility: expose posts flow for UI/consumers.
 */
class GetPostsFlowUseCase(
    private val postRepository: PostRepository,
) {
    operator fun invoke(): Flow<List<Post>> = postRepository.getPosts()
}
