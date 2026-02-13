package org.cpimtn.marxist.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.cpimtn.marxist.android.data.database.dao.PostDao
import org.cpimtn.marxist.android.data.database.entity.PostEntity
import org.cpimtn.marxist.android.data.database.entity.Converters

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
