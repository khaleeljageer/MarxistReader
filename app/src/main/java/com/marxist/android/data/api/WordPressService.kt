package com.marxist.android.data.api

import com.marxist.android.data.model.WPPost
import retrofit2.Response
import retrofit2.http.*

interface WordPressService {
    @GET("posts")
    suspend fun getPosts(
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Response<List<WPPost>>

    @GET("posts")
    suspend fun searchByTerms(
        @Query("search") key: String,
        @Query("per_page") page: Int
    ): Response<List<WPPost>>

    @FormUrlEncoded
    @POST("e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse")
    suspend fun postFeedBack(
        @Header("accept") type: String,
        @Field("entry_1191964018") name: String,
        @Field("entry_663380355") email: String,
        @Field("entry_866777428") comments: String
    ): Response<Any>
}