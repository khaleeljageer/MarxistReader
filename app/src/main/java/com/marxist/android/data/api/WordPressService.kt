package com.marxist.android.data.api

import com.marxist.android.data.model.WPPost
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WordPressService {
    @GET("posts")
    suspend fun getPosts(
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Response<List<WPPost>>

    @GET("posts")
    suspend fun searchByTerms(
        @Query("search") key: String,
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Response<List<WPPost>>
}