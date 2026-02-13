package org.cpimtn.marxist.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.cpimtn.marxist.network.api.WPApiService
import org.cpimtn.marxist.network.downloader.FileDownloader
import org.cpimtn.marxist.network.downloader.FileDownloaderImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .connectTimeout(timeout = 60L, TimeUnit.SECONDS)
                .readTimeout(timeout = 60L, TimeUnit.SECONDS)
                .writeTimeout(timeout = 60L, TimeUnit.SECONDS)
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): WPApiService {
            return retrofit.create(WPApiService::class.java)
        }
    }
}
