package org.cpimtn.marxist.android.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.cpimtn.marxist.android.data.database.converters.ListConverters

@Entity(tableName = "posts")
@TypeConverters(ListConverters::class)
data class PostEntity(
    @PrimaryKey
    val id: Int,
    val date: String,
    val slug: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val tagsId: List<Int>,
    val categoriesId: List<Int>
)
