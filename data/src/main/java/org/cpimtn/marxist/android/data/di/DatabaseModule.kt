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
import org.cpimtn.marxist.android.data.source.local.database.AppDatabase
import org.cpimtn.marxist.android.data.source.local.database.dao.BookDao
import org.cpimtn.marxist.android.data.source.local.database.dao.BookReaderLinkDao
import org.cpimtn.marxist.android.data.source.local.database.dao.CategoryDao
import org.cpimtn.marxist.android.data.source.local.database.dao.PostDao
import org.cpimtn.marxist.android.data.source.local.database.dao.SavedPostDao
import org.cpimtn.marxist.android.data.source.local.database.dao.SearchDao
import org.cpimtn.marxist.android.data.source.local.database.dao.TagDao
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

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS saved_posts (postId INTEGER NOT NULL PRIMARY KEY)"
        )
    }
}

private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS books (" +
                "id TEXT NOT NULL PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "imageUrl TEXT NOT NULL, " +
                "epubUrl TEXT NOT NULL, " +
                "position INTEGER NOT NULL)"
        )
    }
}

private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS book_reader_links (" +
                "bookId TEXT NOT NULL PRIMARY KEY, " +
                "readerId INTEGER NOT NULL)"
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_4_5, MIGRATION_5_6).build()
    }

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao = appDatabase.postDao()

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Provides
    fun provideTagDao(appDatabase: AppDatabase): TagDao = appDatabase.tagDao()

    @Provides
    fun provideSavedPostDao(appDatabase: AppDatabase): SavedPostDao = appDatabase.savedPostDao()

    @Provides
    fun provideSearchDao(appDatabase: AppDatabase): SearchDao = appDatabase.searchDao()

    @Provides
    fun provideBookDao(appDatabase: AppDatabase): BookDao = appDatabase.bookDao()

    @Provides
    fun provideBookReaderLinkDao(appDatabase: AppDatabase): BookReaderLinkDao =
        appDatabase.bookReaderLinkDao()
}