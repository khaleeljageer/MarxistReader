package com.marxist.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.marxist.android.database.entities.LocalFeeds

@Dao
interface LocalFeedsDao {
    @Query("SELECT * FROM localFeeds ORDER BY pub_date DESC")
    fun getAllFeeds(): LiveData<MutableList<LocalFeeds>>

    @Query("SELECT * FROM localFeeds WHERE is_bookmarked=:isBookMarked ORDER BY pub_date DESC")
    fun getBookMarks(isBookMarked: Boolean): LiveData<MutableList<LocalFeeds>>

    @Query("SELECT count(*) FROM localFeeds")
    fun getFeedsCount(): Int

    @Insert(onConflict = IGNORE)
    fun insert(localBooks: LocalFeeds)

    @Query("UPDATE localFeeds SET is_bookmarked = :isBookMarked WHERE title = :title AND pub_date = :pubDate")
    fun updateBookMarkStatus(isBookMarked: Boolean, title: String, pubDate: Long)

    @Query("UPDATE localFeeds SET downloaded_path = :filePath WHERE title = :title AND pub_date = :pubDate")
    fun updateAudioStatus(filePath: String, title: String, pubDate: Long)

    @Query("UPDATE localFeeds SET is_downloaded = :status WHERE title = :title")
    fun updateAudioStatus(status: Boolean, title: String)

    @Query("UPDATE localFeeds SET is_downloaded = :status, downloaded_path=:path WHERE title = :title AND pub_date = :pubDate")
    fun resetAudioStatus(status: Boolean, path: String, title: String, pubDate: Long)

    @Query("SELECT * FROM localFeeds WHERE is_downloaded=:status ORDER BY pub_date DESC")
    fun getDownloaded(status: Boolean): LiveData<MutableList<LocalFeeds>>
}