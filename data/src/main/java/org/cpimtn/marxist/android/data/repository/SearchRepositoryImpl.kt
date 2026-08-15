package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.SearchDao
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.SearchRepository
import java.util.Locale
import javax.inject.Inject

/**
 * FTS-based search over local posts. Builds an escaped, prefix-matching FTS4
 * MATCH expression from the raw query, then re-ranks the matches by relevance
 * (FTS4 exposes no rank column) before returning domain [Post]s.
 */
class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao,
) : SearchRepository {

    override fun search(query: String, limit: Int): Flow<List<Post>> {
        val tokens = tokenize(query)
        val match = tokens.toMatchExpression()
        return if (match.isBlank()) {
            flowOf(emptyList())
        } else {
            searchDao.search(match, candidateLimit(limit))
                .map { entities ->
                    entities.map { it.toDomain() }
                        .rankByRelevance(query, tokens)
                        .take(limit)
                }
                .catch { emit(emptyList()) }
        }
    }

    override fun searchTitles(query: String, limit: Int): Flow<List<Post>> {
        val tokens = tokenize(query)
        val match = tokens.toMatchExpression()
        return if (match.isBlank()) {
            flowOf(emptyList())
        } else {
            searchDao.searchTitles("title:($match)", candidateLimit(limit))
                .map { entities ->
                    entities.map { it.toDomain() }
                        .rankByRelevance(query, tokens)
                        .take(limit)
                }
                .catch { emit(emptyList()) }
        }
    }

    /**
     * Splits a raw string into letter/mark/digit tokens, dropping FTS4 operator
     * syntax like `-`, `:`, quotes, and parens so a raw query can never be
     * interpreted as NOT/AND/OR/column-filter syntax.
     *
     * `\p{M}` (combining marks) MUST be included: in Tamil the vowel signs and
     * virama/pulli (e.g. ி, ்) are marks, not letters, so omitting them would
     * split a word like "சிந்தன்" into "ச", "ந", "தன" — three over-broad prefix
     * tokens that match almost anything. Keeping marks preserves whole words.
     */
    private fun tokenize(s: String): List<String> =
        TOKEN_REGEX.findAll(s).map { it.value.lowercase(Locale.ROOT) }.toList()

    /** Prefix-matching FTS4 MATCH expression, e.g. ["முதலா"] -> "முதலா*". */
    private fun List<String>.toMatchExpression(): String =
        joinToString(" ") { "$it*" }

    /**
     * Fetch more rows than we ultimately return so ranking has something to
     * choose from — the DAO can only pre-order by id, so trimming there first
     * would throw away the most relevant matches before we ever score them.
     */
    private fun candidateLimit(limit: Int): Int =
        (limit * CANDIDATE_MULTIPLIER).coerceIn(limit, MAX_CANDIDATES)

    /**
     * Re-orders matches so the ones most relevant to the query come first.
     * Title hits outweigh content hits, whole-word beats prefix, and the full
     * phrase appearing in the title wins. Stable within a score by title length
     * (more focused titles first).
     */
    private fun List<Post>.rankByRelevance(query: String, tokens: List<String>): List<Post> {
        if (tokens.isEmpty()) return this
        val phrase = query.trim().lowercase(Locale.ROOT)
        return sortedWith(
            compareByDescending<Post> { it.relevanceScore(phrase, tokens) }
                .thenBy { it.title.length }
        )
    }

    private fun Post.relevanceScore(phrase: String, tokens: List<String>): Int {
        val title = title.lowercase(Locale.ROOT)
        val content = content.lowercase(Locale.ROOT)
        val titleWords = tokenize(title).toHashSet()

        var score = 0
        if (phrase.isNotBlank() && title.contains(phrase)) score += PHRASE_IN_TITLE
        if (phrase.isNotBlank() && content.contains(phrase)) score += PHRASE_IN_CONTENT
        for (token in tokens) {
            when {
                titleWords.contains(token) -> score += TITLE_WHOLE_WORD
                title.contains(token) -> score += TITLE_PREFIX
            }
            if (content.contains(token)) score += CONTENT_HIT
        }
        return score
    }

    private companion object {
        val TOKEN_REGEX = Regex("[\\p{L}\\p{M}\\p{N}]+")

        const val CANDIDATE_MULTIPLIER = 4
        const val MAX_CANDIDATES = 300

        const val PHRASE_IN_TITLE = 1000
        const val PHRASE_IN_CONTENT = 60
        const val TITLE_WHOLE_WORD = 120
        const val TITLE_PREFIX = 80
        const val CONTENT_HIT = 8
    }
}
