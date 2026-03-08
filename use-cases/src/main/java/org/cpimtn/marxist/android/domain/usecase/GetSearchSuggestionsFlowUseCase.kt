package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import org.cpimtn.marxist.android.domain.repository.SearchRepository
import org.cpimtn.marxist.android.domain.repository.TagRepository

/**
 * Title-only FTS suggestions (e.g. while typing) as [FeedItem].
 */
class GetSearchSuggestionsFlowUseCase(
    private val searchRepository: SearchRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
) {
    operator fun invoke(query: String, limit: Int = 10): Flow<List<FeedItem>> =
        combine(
            searchRepository.searchTitles(query, limit),
            categoryRepository.getCategories().map { list -> list.associate { it.id to it.name } },
            tagRepository.getTags().map { list -> list.associate { it.id to it.name } },
        ) { posts, categoryNames, tagNames ->
            posts.map { post ->
                FeedItem(
                    post = post,
                    categoryLabel = post.categories.firstOrNull()?.let { categoryNames[it] ?: "" }
                        ?.takeIf { it.isNotBlank() } ?: "",
                    tagLabels = post.tags.take(3).mapNotNull { tagNames[it] }.filter { it.isNotBlank() },
                )
            }
        }
}
