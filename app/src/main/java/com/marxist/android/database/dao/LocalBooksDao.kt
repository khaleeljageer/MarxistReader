package com.marxist.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marxist.android.database.entities.LocalBooks

@Dao
interface LocalBooksDao {
    @Query("SELECT * from localBooks")
    fun getAllLocalBooks(): LiveData<MutableList<LocalBooks>>

    @Query("SELECT * from localBooks")
    fun getAllBooks(): MutableList<LocalBooks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(localBooks: LocalBooks)

    @Delete
    fun deleteBook(localBooks: LocalBooks)

    @Query("SELECT EXISTS(SELECT bookid from localBooks WHERE bookid = :bookId)")
    fun isIdAvailable(bookId: String): Boolean

    @Query("SELECT * from localBooks WHERE bookid = :bookId")
    fun getBook(bookId: String): LocalBooks
}