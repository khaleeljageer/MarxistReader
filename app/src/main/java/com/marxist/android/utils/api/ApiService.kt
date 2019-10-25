package com.marxist.android.utils.api

import com.marxist.android.model.RssFeed
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("feed/")
    fun getFeeds(@Query("paged") page: Int): Observable<RssFeed>
}