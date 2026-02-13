package org.cpimtn.marxist.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.database.entity.PostEntity

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts ORDER BY date DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    /**
     * Replaces all posts in one transaction so sync never leaves DB empty on failure.
     */
    @androidx.room.Transaction
    suspend fun replaceAll(entities: List<PostEntity>) {
        clearAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
