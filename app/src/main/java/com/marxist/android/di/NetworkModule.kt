package com.marxist.android.di

import android.content.Context
import com.marxist.android.utils.CacheInterceptor
import com.marxist.android.utils.api.ApiService
import com.marxist.android.utils.api.GitHubService
import com.marxist.android.utils.api.UnsafeOkHttpClient
import okhttp3.Cache
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File

val networkModule = module {
    single { createCache(get()) }
    single { getGitHubService(get(), get()) }
    single { getApiService(get(), get()) }
}

fun createCache(context: Context): Cache {
    val httpCacheDirectory = File(context.cacheDir, "responses");
    val cacheSize: Long = 100 * 1024 * 1024 // 100 MiB
    return Cache(httpCacheDirectory, cacheSize)
}

fun getGitHubService(context: Context, cache: Cache): GitHubService {
    val okHttp = UnsafeOkHttpClient.unsafeOkHttpClient
    okHttp.addNetworkInterceptor(CacheInterceptor(context))
    okHttp.cache(cache)

    return Retrofit.Builder()
        .client(okHttp.build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://raw.githubusercontent.com/tamilmarxist/MarxistTamilEbooks/")
        .build().create(GitHubService::class.java)
}

fun getApiService(context: Context, cache: Cache): ApiService {
    val okHttp = UnsafeOkHttpClient.unsafeOkHttpClient
    okHttp.addNetworkInterceptor(CacheInterceptor(context))
    okHttp.cache(cache)

    return Retrofit.Builder()
        .client(okHttp.build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .baseUrl("https://marxistreader.home.blog/").build()
        .create(ApiService::class.java)
}
