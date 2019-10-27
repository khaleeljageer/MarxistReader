@file:Suppress("DEPRECATION")

package com.marxist.android.utils.api

import com.marxist.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object ApiClient {
    lateinit var mApiService: ApiService

    fun setApiService() {
        mApiService = Retrofit.Builder()
            .client(UnsafeOkHttpClient.unsafeOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .baseUrl("https://marxistreader.home.blog/").build().create(ApiService::class.java)
    }
}