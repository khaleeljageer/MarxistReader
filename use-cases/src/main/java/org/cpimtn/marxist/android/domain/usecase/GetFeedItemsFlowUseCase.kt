package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.Post

/**
 * Use case: observe feed items (posts with resolved category and tag labels for display).
 * Resolves at most one category and up to three tags per post; logic lives here instead of UI.
 */
class GetFeedItemsFlowUseCase(
    private val getPostsFlowUseCase: GetPostsFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val getTagsFlowUseCase: GetTagsFlowUseCase,
) {
    operator fun invoke(): Flow<List<FeedItem>> =
        combine(
            getPostsFlowUseCase(),
            getCategoriesFlowUseCase().map { list -> list.associate { it.id to it.name } },
            getTagsFlowUseCase().map { list -> list.associate { it.id to it.name } },
        ) { posts, categoryNames, tagNames ->
            posts.map { post -> post.toFeedItem(categoryNames, tagNames) }
        }

    private fun Post.toFeedItem(
        categoryNames: Map<Int, String>,
        tagNames: Map<Int, String>
    ): FeedItem =
        FeedItem(
            post = this,
            categoryLabel = categories.firstOrNull()?.let { categoryNames[it] ?: "" }
                ?.takeIf { it.isNotBlank() } ?: "",
            tagLabels = tags.take(3).mapNotNull { tagNames[it] }.filter { it.isNotBlank() },
        )
}
