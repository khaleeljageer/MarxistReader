package com.marxist.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.database.entities.LocalNotifications

@Dao
interface LocalNotificationsDao {
    @Query("SELECT * from localNotifications ORDER BY timeStamp DESC")
    fun getAllNotifications(): LiveData<MutableList<LocalNotifications>>

    @Query("SELECT count(title) from localNotifications WHERE title= :title")
    fun getTitleCount(title: String): Int

    @Insert(onConflict = IGNORE)
    fun insert(notifications: LocalNotifications)

    @Delete
    fun deleteNotification(notifications: LocalNotifications)
}