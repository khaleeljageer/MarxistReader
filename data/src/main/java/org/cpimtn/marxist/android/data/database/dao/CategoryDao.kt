package org.cpimtn.marxist.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.database.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name")
    fun getAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun clearAll()

    @androidx.room.Transaction
    suspend fun replaceAll(entities: List<CategoryEntity>) {
        clearAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
