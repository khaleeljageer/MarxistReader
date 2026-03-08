package org.cpimtn.marxist.android.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.source.local.database.entity.PostEntity

@Dao
interface SearchDao {

    /**
     * Full-text search across title, excerpt, and content.
     *
     * MATCH query uses prefix matching (query*) so "முதலா" matches
     * "முதலாளித்துவம்", "முதலாளி", etc.
     *
     * Results ordered by FTS rank (relevance).
     * rank = bm25(posts_fts) — built-in FTS4 ranking function.
     *
     * JOIN on rowid is how Room connects FTS to content table.
     */
    @Query(
        """
        SELECT p.* FROM posts p
        INNER JOIN posts_fts f ON p.rowid = f.rowid
        WHERE posts_fts MATCH :query
        ORDER BY rank
        LIMIT :limit
        """
    )
    fun search(query: String, limit: Int = 50): Flow<List<PostEntity>>

    /**
     * Search only titles — used for instant suggestions while typing.
     * Lighter query, fewer results.
     */
    @Query(
        """
        SELECT p.* FROM posts p
        INNER JOIN posts_fts f ON p.rowid = f.rowid
        WHERE posts_fts MATCH 'title:' || :query
        ORDER BY rank
        LIMIT :limit
        """
    )
    fun searchTitles(query: String, limit: Int = 10): Flow<List<PostEntity>>

    /**
     * Rebuild FTS index after bulk insert/update.
     * Call this after sync completes.
     */
    @Query("INSERT INTO posts_fts(posts_fts) VALUES('rebuild')")
    suspend fun rebuildIndex()
}