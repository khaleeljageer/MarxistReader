package org.cpimtn.marxist.android.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: String,
    val imageUrl: String,
    val epubUrl: String,
    /** Preserves catalog order from the source JSON; Room gives no ordering guarantee otherwise. */
    val position: Int,
)
