package com.marxist.android.data.api

import com.marxist.android.model.BooksResponse
import com.marxist.android.model.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET

interface GitHubService {
    @GET("master/booksdb.json")
    suspend fun getBooks(): Response<BooksResponse>

    @GET("master/notifications.json")
    suspend fun getNotifications(): Response<NotificationResponse>
}