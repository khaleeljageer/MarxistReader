package org.cpimtn.marxist.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.cpimtn.marxist.core.config.AppConfig
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
            val n = AppConfig.Network
            return OkHttpClient.Builder()
                .connectTimeout(n.CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(n.READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(n.WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
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
                .baseUrl(AppConfig.Network.BASE_URL)
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
