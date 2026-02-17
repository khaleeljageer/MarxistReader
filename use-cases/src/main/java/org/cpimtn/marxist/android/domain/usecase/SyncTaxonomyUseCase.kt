package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import org.cpimtn.marxist.android.domain.repository.TagRepository

/**
 * Syncs categories and tags from the API into local DB (with pagination), in parallel.
 * Call on app launch so IDs can be resolved to names for posts.
 */
class SyncTaxonomyUseCase(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        coroutineScope {
            val categories = async { categoryRepository.syncCategories().getOrThrow() }
            val tags = async { tagRepository.syncTags().getOrThrow() }
            categories.await()
            tags.await()
        }
    }
}
