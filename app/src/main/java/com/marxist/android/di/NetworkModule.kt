package com.marxist.android.di

import com.marxist.android.utils.api.ApiService
import com.marxist.android.utils.api.GitHubService
import com.marxist.android.utils.api.UnsafeOkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

val networkModule = module {
    single { getApiService() }
    single { getGitHubService() }
}

fun getGitHubService(): GitHubService {
    return Retrofit.Builder()
        .client(UnsafeOkHttpClient.unsafeOkHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://raw.githubusercontent.com/tamilmarxist/MarxistTamilEbooks/")
        .build().create(GitHubService::class.java)
}

fun getApiService(): ApiService {
    return Retrofit.Builder().client(UnsafeOkHttpClient.unsafeOkHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .baseUrl("https://marxistreader.home.blog/").build()
        .create(ApiService::class.java)
}
