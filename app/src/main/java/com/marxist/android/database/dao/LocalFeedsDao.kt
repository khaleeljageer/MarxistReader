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

    @Query("SELECT count(*) FROM localFeeds")
    fun getFeedsCount(): Int

    @Insert(onConflict = IGNORE)
    fun insert(localBooks: LocalFeeds)

    @Query("UPDATE localFeeds SET downloaded_path = :filePath WHERE title = :title AND pub_date = :pubDate")
    fun updateAudioStatus(filePath: String, title: String, pubDate: Long)

    @Query("UPDATE localFeeds SET is_downloaded = :status, downloaded_path=:path WHERE title = :title")
    fun updateAudioStatus(status: Boolean, path: String, title: String)

    @Query("UPDATE localFeeds SET is_downloaded = :status, downloaded_path=:path WHERE title = :title AND pub_date = :pubDate")
    fun resetAudioStatus(status: Boolean, path: String, title: String, pubDate: Long)

    @Query("SELECT * FROM localFeeds WHERE is_downloaded=:status ORDER BY pub_date DESC")
    fun getDownloaded(status: Boolean): MutableList<LocalFeeds>

    @Query("SELECT is_downloaded FROM localFeeds WHERE title =:title")
    fun getDownloadStatus(title: String): Boolean

    @Query("SELECT downloaded_path FROM localFeeds WHERE title =:title")
    fun getDownloadedPath(title: String): String
}