package com.marxist.android.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "localFeeds")
data class LocalFeeds(
    @PrimaryKey
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "pub_date")
    val pubDate: Long,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String,

    @ColumnInfo(name = "is_downloaded")
    var isDownloaded: Boolean,

    @ColumnInfo(name = "is_bookmarked")
    var isBookMarked: Boolean,

    @ColumnInfo(name = "read_percent")
    var readPercent: Int
) : Serializable