@file:Suppress("DEPRECATION")

package com.marxist.android.utils.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object ApiClient {
    lateinit var mApiService: ApiService
    lateinit var mGitHubService: GitHubService
    lateinit var mDownloadService: DownloadService

    fun setApiService() {
        mApiService = Retrofit.Builder()
            .client(UnsafeOkHttpClient.unsafeOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .baseUrl("https://marxistreader.home.blog/").build().create(ApiService::class.java)
    }

    fun setGitHubService() {
        mGitHubService = Retrofit.Builder()
            .client(UnsafeOkHttpClient.unsafeOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/tamilmarxist/MarxistTamilEbooks/")
            .build().create(GitHubService::class.java)
    }


    fun setDownloadService() {
        mDownloadService = Retrofit.Builder()
            .client(UnsafeOkHttpClient.unsafeOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://archive.org/download/")
            .build().create(DownloadService::class.java)
    }
}