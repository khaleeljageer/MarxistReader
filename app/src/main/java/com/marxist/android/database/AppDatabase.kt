package com.marxist.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marxist.android.database.dao.LocalBooksDao
import com.marxist.android.database.dao.LocalFeedsDao
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.database.entities.LocalFeeds

@Database(
    entities = [LocalFeeds::class, LocalBooks::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localFeedsDao(): LocalFeedsDao
    abstract fun localBooksDao(): LocalBooksDao

    companion object {
        private var instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "marxist_reader.db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }
    }
}