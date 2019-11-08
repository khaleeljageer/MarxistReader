package com.marxist.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.marxist.android.database.entities.LocalHighlights

@Dao
interface LocalHighlightsDao {
    @Query("SELECT * from localHighlights ORDER BY timeStamp DESC")
    fun getAllHighlights(): LiveData<MutableList<LocalHighlights>>

    @Query("SELECT count(title) from localHighlights WHERE title= :title")
    fun getTitleCount(title: String): Int

    @Insert(onConflict = IGNORE)
    fun insert(highLight: LocalHighlights)

    @Delete
    fun deleteHighlight(highLight: LocalHighlights)
}