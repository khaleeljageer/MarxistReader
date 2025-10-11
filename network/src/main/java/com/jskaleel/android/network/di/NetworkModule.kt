package com.jskaleel.android.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import com.jskaleel.android.network.api.WPApiService
import com.jskaleel.android.network.downloader.FileDownloader
import com.jskaleel.android.network.downloader.FileDownloaderImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindFileDownloader(
        fileDownloaderImpl: FileDownloaderImpl
    ): FileDownloader

    companion object {
        const val BASE_URL = "https://marxist.cpimtn.org/wp-json/wp/v2/"

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(timeout = 60L, TimeUnit.SECONDS)
                .readTimeout(timeout = 60L, TimeUnit.SECONDS)
                .writeTimeout(timeout = 60L, TimeUnit.SECONDS)
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(client: OkHttpClient): Retrofit {
            return Retrofit.Builder().baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): WPApiService {
            return retrofit.create(WPApiService::class.java)
        }
    }
}
