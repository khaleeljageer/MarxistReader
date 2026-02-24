package org.cpimtn.marxist.android.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.source.local.database.entity.SavedPostEntity

@Dao
interface SavedPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SavedPostEntity)

    @Query("DELETE FROM saved_posts WHERE postId = :postId")
    suspend fun deleteByPostId(postId: Int)

    @Query("SELECT postId FROM saved_posts")
    fun getAllSavedPostIds(): Flow<List<Int>>
}
