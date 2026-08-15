package org.cpimtn.marxist.android.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.cpimtn.marxist.android.data.source.local.database.converters.ListConverters
import org.cpimtn.marxist.android.data.source.local.database.dao.BookDao
import org.cpimtn.marxist.android.data.source.local.database.dao.BookReaderLinkDao
import org.cpimtn.marxist.android.data.source.local.database.dao.CategoryDao
import org.cpimtn.marxist.android.data.source.local.database.dao.PostDao
import org.cpimtn.marxist.android.data.source.local.database.dao.SavedPostDao
import org.cpimtn.marxist.android.data.source.local.database.dao.SearchDao
import org.cpimtn.marxist.android.data.source.local.database.dao.TagDao
import org.cpimtn.marxist.android.data.source.local.database.entity.BookEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.BookReaderLinkEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.CategoryEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.PostEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.PostFts
import org.cpimtn.marxist.android.data.source.local.database.entity.SavedPostEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.TagEntity

@Database(
    entities = [
        PostEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        SavedPostEntity::class,
        PostFts::class,
        BookEntity::class,
        BookReaderLinkEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun savedPostDao(): SavedPostDao
    abstract fun searchDao(): SearchDao
    abstract fun bookDao(): BookDao
    abstract fun bookReaderLinkDao(): BookReaderLinkDao
}
