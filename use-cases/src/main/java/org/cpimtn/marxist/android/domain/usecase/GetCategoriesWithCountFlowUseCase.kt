package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.CategoryWithCount

/**
 * Categories with post count for search/browse by category. Counts posts that have each category in their categories list.
 */
class GetCategoriesWithCountFlowUseCase(
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val getPostsFlowUseCase: GetPostsFlowUseCase,
) {
    operator fun invoke(): Flow<List<CategoryWithCount>> =
        combine(
            getCategoriesFlowUseCase(),
            getPostsFlowUseCase(),
        ) { categories, posts ->
            categories.map { category ->
                val count = posts.count { post -> category.id in post.categories }
                CategoryWithCount(category = category, postCount = count)
            }.filter { it.postCount > 0 }
                .sortedByDescending { it.postCount }
        }
}
