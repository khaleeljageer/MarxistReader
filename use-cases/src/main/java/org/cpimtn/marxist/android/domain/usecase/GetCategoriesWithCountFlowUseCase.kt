package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.domain.model.Post

/**
 * Categories with post count for search/browse by category. Counts posts that have each category in their categories list.
 *
 * Takes [postsFlow] rather than depending on [GetPostsFlowUseCase] directly so a caller with
 * multiple post-derived use cases (e.g. search's timeline/category browsing) can share one
 * upstream posts subscription instead of each use case re-querying the full posts table.
 */
class GetCategoriesWithCountFlowUseCase(
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
) {
    operator fun invoke(postsFlow: Flow<List<Post>>): Flow<List<CategoryWithCount>> =
        combine(
            getCategoriesFlowUseCase(),
            postsFlow,
        ) { categories, posts ->
            categories.map { category ->
                val count = posts.count { post -> category.id in post.categories }
                CategoryWithCount(category = category, postCount = count)
            }.filter { it.postCount > 0 }
                .sortedByDescending { it.postCount }
        }
}
