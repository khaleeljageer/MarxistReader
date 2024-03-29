package com.marxist.android.utils.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("e/1FAIpQLSfTqVr8z77QxxclC_ZnvsQfOc67F1Wjw07giA2jxoXpFtuvLg/formResponse")
    fun postFeedBack(
        @Header("accept") type: String,
        @Field("entry_1191964018") name: String,
        @Field("entry_663380355") email: String,
        @Field("entry_866777428") comments: String
    ): Observable<ResponseBody>
}