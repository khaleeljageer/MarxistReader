package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Persists recent search queries for the search screen (most recent first).
 */
interface RecentSearchRepository {

    /** Latest queries first, up to [maxSize]. */
    fun getRecentSearches(maxSize: Int = 10): Flow<List<String>>

    /** Prepends [query] and keeps at most [maxSize] entries. */
    suspend fun addRecentSearch(query: String, maxSize: Int = 10)

    suspend fun clearRecentSearches()
}
