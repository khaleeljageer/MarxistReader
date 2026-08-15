package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.Post

/**
 * Posts published in a given month, in feed order (date desc), for browsing
 * from Search > Discovery's timeline chips.
 *
 * [monthKey] matches [TimelineMonth.key][org.cpimtn.marxist.android.domain.model.TimelineMonth.key]
 * ("yyyy-MM"), the same prefix of [Post.date] used to build the timeline.
 *
 * Takes [postsFlow] rather than depending on [GetPostsFlowUseCase] directly so a caller with
 * multiple post-derived use cases (e.g. search's timeline/category browsing) can share one
 * upstream posts subscription instead of each use case re-querying the full posts table.
 */
class GetPostsByMonthFlowUseCase(
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val getTagsFlowUseCase: GetTagsFlowUseCase,
) {
    operator fun invoke(postsFlow: Flow<List<Post>>, monthKey: String): Flow<List<FeedItem>> =
        combine(
            postsFlow,
            getCategoriesFlowUseCase().map { list -> list.associate { it.id to it.name } },
            getTagsFlowUseCase().map { list -> list.associate { it.id to it.name } },
        ) { posts, categoryNames, tagNames ->
            posts.filter { it.date.trim().take(7) == monthKey }.map { post ->
                post.toFeedItem(categoryNames, tagNames)
            }
        }

    private fun Post.toFeedItem(
        categoryNames: Map<Int, String>,
        tagNames: Map<Int, String>,
    ): FeedItem =
        FeedItem(
            post = this,
            categoryLabel = categories.firstOrNull()?.let { categoryNames[it] ?: "" }
                ?.takeIf { it.isNotBlank() } ?: "",
            tagLabels = tags.take(3).mapNotNull { tagNames[it] }.filter { it.isNotBlank() },
        )
}
