package org.cpimtn.marxist.android.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.source.local.database.entity.TagEntity

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name")
    fun getAll(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TagEntity>)

    @Query("DELETE FROM tags")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(entities: List<TagEntity>) {
        clearAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
