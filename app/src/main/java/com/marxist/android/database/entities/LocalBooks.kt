package com.marxist.android.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "localBooks")
data class LocalBooks(
    @ColumnInfo(name = "title")
    val title: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "bookid")
    var bookid: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "epub")
    val epub: String,
    @ColumnInfo(name = "pubDate")
    var pubDate: Long,

    @ColumnInfo(name = "is_downloaded")
    var isDownloaded: Boolean,
    @ColumnInfo(name = "download_id")
    var downloadId: Long = 0,
    @ColumnInfo(name = "download_percent")
    var downloadPercent: Long = 0,
    @ColumnInfo(name = "saved_path")
    var savedPath: String = "",

    var lastEmittedDownloadPercent: Long = -1,
    var isExpanded: Boolean = false,
    var date: String
) : Serializable