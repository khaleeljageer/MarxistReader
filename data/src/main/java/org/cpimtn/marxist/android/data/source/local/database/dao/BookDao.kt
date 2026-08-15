package org.cpimtn.marxist.android.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.data.source.local.database.entity.BookEntity

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY position ASC")
    fun getAll(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun clearAll()

    /**
     * Replaces all books in one transaction so sync never leaves DB empty on failure.
     */
    @Transaction
    suspend fun replaceAll(entities: List<BookEntity>) {
        clearAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
