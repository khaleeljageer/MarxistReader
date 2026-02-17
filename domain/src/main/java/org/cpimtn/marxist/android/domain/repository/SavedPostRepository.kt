package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Local source of truth for saved (bookmarked) post IDs. Offline-first.
 */
interface SavedPostRepository {
    fun getSavedPostIds(): Flow<Set<Int>>
    suspend fun save(postId: Int)
    suspend fun unsave(postId: Int)
}
