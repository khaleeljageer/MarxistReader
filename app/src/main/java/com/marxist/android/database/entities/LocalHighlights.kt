package com.marxist.android.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "localHighlights")
data class LocalHighlights(
    @PrimaryKey
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "highlight")
    @TypeConverters
    val highlight: String
) : Serializable