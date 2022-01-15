package com.marxist.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marxist.android.database.dao.LocalBooksDao
import com.marxist.android.database.entities.LocalBooks

@Database(
    entities = [LocalBooks::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

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