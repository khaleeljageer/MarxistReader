package org.cpimtn.marxist.android.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.cpimtn.marxist.android.data.source.local.database.entity.BookReaderLinkEntity

@Dao
interface BookReaderLinkDao {

    @Query("SELECT readerId FROM book_reader_links WHERE bookId = :bookId")
    suspend fun getReaderId(bookId: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BookReaderLinkEntity)
}
