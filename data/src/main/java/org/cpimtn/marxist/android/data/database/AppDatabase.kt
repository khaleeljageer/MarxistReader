package org.cpimtn.marxist.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.cpimtn.marxist.android.data.database.dao.PostDao
import org.cpimtn.marxist.android.data.database.converters.ListConverters
import org.cpimtn.marxist.android.data.database.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
