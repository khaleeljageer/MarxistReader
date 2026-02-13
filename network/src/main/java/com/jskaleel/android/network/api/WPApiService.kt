package com.jskaleel.android.network.api

import com.jskaleel.android.network.model.PostDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WPApiService {

    @GET(value = "posts")
    suspend fun getPageInfo(
        @Query("_fields") fields: String = "id",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<String>>

    @GET(value = "posts")
    suspend fun getPosts(
        @Query("_fields") fields: String = "id,date,slug,excerpt,title,tags_names,categories_names",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<PostDTO>>
}
