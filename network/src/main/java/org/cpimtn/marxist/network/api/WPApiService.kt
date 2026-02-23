package org.cpimtn.marxist.network.api

import org.cpimtn.marxist.network.model.CategoryDTO
import org.cpimtn.marxist.network.model.PostDTO
import org.cpimtn.marxist.network.model.TagDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WPApiService {
    @GET(value = "posts")
    suspend fun getPosts(
        @Query("_fields") fields: String = "id,date,slug,excerpt,title,content,tags,categories",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<PostDTO>>

    @GET("categories")
    suspend fun getCategories(
        @Query("_fields") fields: String = "id,name",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int,
    ): Response<List<CategoryDTO>>

    @GET("tags")
    suspend fun getTags(
        @Query("_fields") fields: String = "id,name",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int,
    ): Response<List<TagDTO>>
}
