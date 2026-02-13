package org.cpimtn.marxist.android.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.data.database.AppDatabase
import org.cpimtn.marxist.android.data.database.dao.CategoryDao
import org.cpimtn.marxist.android.data.database.dao.PostDao
import org.cpimtn.marxist.android.data.database.dao.TagDao
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS categories (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS tags (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL)"
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "marxist-reader-db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao = appDatabase.postDao()

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Provides
    fun provideTagDao(appDatabase: AppDatabase): TagDao = appDatabase.tagDao()
}