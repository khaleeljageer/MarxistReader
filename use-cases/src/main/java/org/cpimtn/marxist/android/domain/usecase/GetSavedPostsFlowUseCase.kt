package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.cpimtn.marxist.android.domain.model.Post

/**
 * Stream of posts that are currently saved (bookmarked), in feed order (date desc).
 */
class GetSavedPostsFlowUseCase(
    private val getPostsFlowUseCase: GetPostsFlowUseCase,
    private val getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
) {
    operator fun invoke(): Flow<List<Post>> =
        combine(
            getPostsFlowUseCase(),
            getSavedPostIdsFlowUseCase(),
        ) { posts, savedIds ->
            posts.filter { it.id in savedIds }
        }
}
