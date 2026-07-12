package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.SearchDao
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * FTS-based search over local posts. Builds an escaped, prefix-matching FTS4
 * MATCH expression from the raw query and maps entities to domain [Post].
 */
class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao,
) : SearchRepository {

    override fun search(query: String, limit: Int): Flow<List<Post>> {
        val match = toMatchExpression(query)
        return if (match.isBlank()) {
            flowOf(emptyList())
        } else {
            searchDao.search(match, limit)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { emit(emptyList()) }
        }
    }

    override fun searchTitles(query: String, limit: Int): Flow<List<Post>> {
        val match = toMatchExpression(query)
        return if (match.isBlank()) {
            flowOf(emptyList())
        } else {
            searchDao.searchTitles("title:($match)", limit)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { emit(emptyList()) }
        }
    }

    /**
     * Extracts letter/digit tokens only (dropping FTS4 operator syntax like
     * `-`, `:`, quotes, and parens so a raw query can never be interpreted as
     * NOT/AND/OR/column-filter syntax), then appends `*` to each token so
     * partial words prefix-match, e.g. "முதலா" -> "முதலா*".
     */
    private fun toMatchExpression(q: String): String =
        Regex("[\\p{L}\\p{N}]+").findAll(q)
            .map { "${it.value}*" }
            .joinToString(" ")
}
