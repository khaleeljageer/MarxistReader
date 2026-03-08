package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.SearchDao
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * FTS-based search over local posts. Sanitizes query for FTS (prefix form)
 * and maps entities to domain [Post].
 */
class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao,
) : SearchRepository {

    override fun search(query: String, limit: Int): Flow<List<Post>> {
        val sanitized = sanitizeQuery(query)
        return if (sanitized.isBlank()) {
            kotlinx.coroutines.flow.flowOf(emptyList())
        } else {
            searchDao.search(sanitized, limit).map { entities -> entities.map { it.toDomain() } }
        }
    }

    override fun searchTitles(query: String, limit: Int): Flow<List<Post>> {
        val sanitized = sanitizeQuery(query)
        return if (sanitized.isBlank()) {
            kotlinx.coroutines.flow.flowOf(emptyList())
        } else {
            searchDao.searchTitles(sanitized, limit).map { entities -> entities.map { it.toDomain() } }
        }
    }

    /**
     * Trim and ensure FTS-friendly form. Caller may append * for prefix match;
     * we avoid empty or purely whitespace queries.
     */
    private fun sanitizeQuery(q: String): String = q.trim().takeIf { it.isNotBlank() } ?: ""
}
