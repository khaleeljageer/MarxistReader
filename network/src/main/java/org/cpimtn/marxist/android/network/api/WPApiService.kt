package org.cpimtn.marxist.android.network.api

import org.cpimtn.marxist.android.network.model.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface WPApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("_fields") fields: String = "id,date,slug,excerpt,title,tags_names,categories_names",
        @Query("page") page: Int
    ): List<Post>
}
