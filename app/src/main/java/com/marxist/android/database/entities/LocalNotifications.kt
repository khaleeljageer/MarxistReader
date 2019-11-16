package com.marxist.android.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "localNotifications")
data class LocalNotifications(
    @PrimaryKey
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String
) : Serializable