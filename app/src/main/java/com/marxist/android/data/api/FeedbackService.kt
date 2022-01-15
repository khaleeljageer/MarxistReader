package com.marxist.android.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by Khaleel Jageer on 16/01/22.
 */
interface FeedbackService {

    @FormUrlEncoded
    @POST("e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse")
    suspend fun postFeedBack(
        @Header("accept") type: String,
        @Field("entry_1191964018") name: String,
        @Field("entry_663380355") email: String,
        @Field("entry_866777428") comments: String
    ): Response<ResponseBody>
}