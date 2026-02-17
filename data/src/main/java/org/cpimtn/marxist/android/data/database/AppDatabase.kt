package org.cpimtn.marxist.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.cpimtn.marxist.android.data.database.dao.CategoryDao
import org.cpimtn.marxist.android.data.database.dao.PostDao
import org.cpimtn.marxist.android.data.database.dao.SavedPostDao
import org.cpimtn.marxist.android.data.database.dao.TagDao
import org.cpimtn.marxist.android.data.database.converters.ListConverters
import org.cpimtn.marxist.android.data.database.entity.CategoryEntity
import org.cpimtn.marxist.android.data.database.entity.PostEntity
import org.cpimtn.marxist.android.data.database.entity.SavedPostEntity
import org.cpimtn.marxist.android.data.database.entity.TagEntity

@Database(
    entities = [PostEntity::class, CategoryEntity::class, TagEntity::class, SavedPostEntity::class],
    version = 3,
    exportSchema = false,
)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun savedPostDao(): SavedPostDao
}
