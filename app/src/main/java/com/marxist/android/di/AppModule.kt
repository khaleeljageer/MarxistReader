package com.marxist.android.di

import android.content.Context
import android.content.SharedPreferences
import com.marxist.android.data.api.FeedbackService
import com.marxist.android.data.api.GitHubService
import com.marxist.android.data.api.WordPressService
import com.marxist.android.database.AppDatabase
import com.marxist.android.utils.AppPreference
import com.marxist.android.utils.EventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus
    }

    @Provides
    @Singleton
    fun provideGitHubService(okHttpClient: OkHttpClient): GitHubService =
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/tamilmarxist/MarxistTamilEbooks/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(GitHubService::class.java)

    @Provides
    @Singleton
    fun provideFeedbackService(okHttpClient: OkHttpClient): FeedbackService =
        Retrofit.Builder()
            .baseUrl(" https://docs.google.com/forms/d/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(FeedbackService::class.java)

    @Provides
    @Singleton
    fun provideWordPressService(okHttpClient: OkHttpClient): WordPressService =
        Retrofit.Builder()
            .baseUrl("https://public-api.wordpress.com/wp/v2/sites/marxistreader.home.blog/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(WordPressService::class.java)

    @Provides
    @Singleton
    fun providePreference(@ApplicationContext context: Context): SharedPreferences {
        return AppPreference.customPrefs(context)
    }

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getAppDatabase(context)
    }

}