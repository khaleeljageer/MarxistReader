package com.marxist.android.data.api

import com.marxist.android.model.BooksResponse
import com.marxist.android.model.QuotesResponse
import retrofit2.Response
import retrofit2.http.GET

interface GitHubService {
    @GET("master/booksdb.json?token=GHSAT0AAAAAABQSWSL6XOELXCBSME5Y3BF2YPC4DUA")
    suspend fun getBooks(): Response<BooksResponse>

    @GET("master/notifications.json?token=GHSAT0AAAAAABQSWSL7OC7B457GHW53A5V4YPC4ANA")
    suspend fun getQuotes(): Response<QuotesResponse>
}