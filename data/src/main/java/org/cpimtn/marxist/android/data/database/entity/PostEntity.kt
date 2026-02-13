package org.cpimtn.marxist.android.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jskaleel.android.network.model.PostDTO
import org.cpimtn.marxist.android.domain.model.Post

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class PostEntity(
    @PrimaryKey
    val id: Int,
    val date: String,
    val slug: String,
    val title: String,
    val excerpt: String,
    val tagsNames: List<String>,
    val categoriesNames: List<String>
)

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}

fun PostDTO.toEntity() = PostEntity(
    id = id,
    date = date,
    slug = slug,
    title = title.rendered,
    excerpt = excerpt.rendered,
    tagsNames = tags,
    categoriesNames = categories
)

fun PostEntity.toDomain() = Post(
    id = id,
    date = date,
    slug = slug,
    title = title,
    excerpt = excerpt,
    tags = tagsNames,
    categories = categoriesNames
)
