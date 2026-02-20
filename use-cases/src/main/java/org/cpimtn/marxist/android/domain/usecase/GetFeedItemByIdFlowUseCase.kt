package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.Post

class GetFeedItemByIdFlowUseCase(
    private val getPostByIdFlowUseCase: GetPostByIdFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val getTagsFlowUseCase: GetTagsFlowUseCase,
) {

    operator fun invoke(postId: Int): Flow<FeedItem?> =
        combine(
            getPostByIdFlowUseCase(postId),
            getCategoriesFlowUseCase().map { list -> list.associate { it.id to it.name } },
            getTagsFlowUseCase().map { list -> list.associate { it.id to it.name } },
        ) { post, categoryNames, tagNames ->
            post?.toFeedItem(categoryNames, tagNames)
        }

    private fun Post.toFeedItem(
        categoryNames: Map<Int, String>,
        tagNames: Map<Int, String>
    ): FeedItem =
        FeedItem(
            post = this,
            categoryLabel = categories.firstOrNull()?.let { categoryNames[it] ?: "" }
                ?.takeIf { it.isNotBlank() } ?: "",
            tagLabels = tags.mapNotNull { tagNames[it] }.filter { it.isNotBlank() },
        )
}