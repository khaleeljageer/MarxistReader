package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Post

/**
 * Domain contract for full-text search over local posts (FTS).
 * Implemented in the data layer via Room FTS4.
 */
interface SearchRepository {
    /** Full-text search across title and content. Returns matching posts. */
    fun search(query: String, limit: Int = 50): Flow<List<Post>>

    /** Search only titles (e.g. for suggestions while typing). */
    fun searchTitles(query: String, limit: Int = 10): Flow<List<Post>>
}
