package com.marxist.android.di

import android.content.Context
import com.marxist.android.BuildConfig
import com.marxist.android.R
import com.marxist.android.utils.getAppUserAgent
import com.marxist.android.utils.isOnline
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val HEADER_CACHE_CONTROL =
        "Cache-Control" // removes Cache-Control header from the server, and apply our own cache control
    private const val HEADER_PRAGMA = "Pragma" // overrides "Not to use caching scenario"
    private const val CACHE_SIZE = 100 * 1024 * 1024.toLong() // 100 MB
    private const val MAX_AGE = 10 // if a request comes within 10s it will show from cache
    private const val MAX_STALE = 15 // No days cache works when offline mode

    @Provides
    @Singleton
    fun providesOkHttpClient(
        cache: Cache,
        @Named("networkInterceptor") networkInterceptor: Interceptor,
        @Named("offlineInterceptor") offlineInterceptor: Interceptor,
        @Named("header") headers: Interceptor
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.apply {
            connectTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)
            readTimeout(2, TimeUnit.MINUTES)
            cache(cache)
            addInterceptor(offlineInterceptor)
            addNetworkInterceptor(networkInterceptor) // only used when network is on
            addInterceptor(headers)
            build()
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            return build()
        }
    }

    @Provides
    @Singleton
    fun providesCache(@ApplicationContext context: Context): Cache {
        return Cache(File(context.cacheDir, context.getString(R.string.app_name)), CACHE_SIZE)
    }

    @Provides
    @Singleton
    @Named("header")
    fun providesHeaders(@ApplicationContext context: Context): Interceptor {
        /**If there are any headers its adds in here */
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", context.getAppUserAgent())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * This interceptor will be called ONLY if the network is available
     */
    @Provides
    @Singleton
    @Named("networkInterceptor")
    fun providesNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(
                    MAX_AGE,
                    TimeUnit.SECONDS
                ) // Cache the response only for 10 sec, so if a request comes within 10sec it will show from cache
                .build()
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL) // removes cache control from the server
                .header(HEADER_CACHE_CONTROL, cacheControl.toString()) // applying our cache control
                .build()
        }
    }

    /**
     * This interceptor will be called both if the network is available and the network is not available
     */

    @Provides
    @Singleton
    @Named("offlineInterceptor")
    fun providesOfflineInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!context.isOnline()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(MAX_STALE, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }
}
