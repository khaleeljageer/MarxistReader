package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.model.SyncResult

/**
 * Domain contract for posts (Clean Architecture).
 * Implemented in the data layer. Offline First: consumers read from local cache;
 * sync with server is done in the background.
 */
interface PostRepository {
    /** Stream of posts from local DB (source of truth). */
    fun getPosts(): Flow<List<Post>>

    /** Runs a full sync from server and writes to local DB. Returns result for UI/retry. */
    suspend fun fullSync(perPage: Int): SyncResult
    fun getPostById(postId: Int): Flow<Post?>
}
