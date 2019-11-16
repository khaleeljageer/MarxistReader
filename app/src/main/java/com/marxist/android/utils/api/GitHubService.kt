package com.marxist.android.utils.api

import com.marxist.android.model.BooksResponse
import com.marxist.android.model.NotificationResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface GitHubService {
    @GET("master/booksdb.json")
    fun getBooks(): Observable<BooksResponse>

    @GET("master/notifications.json")
    fun getNotifications(): Observable<NotificationResponse>
}