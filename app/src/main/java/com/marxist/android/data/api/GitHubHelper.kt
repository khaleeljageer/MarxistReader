package com.marxist.android.data.api

import com.marxist.android.model.BooksResponse
import com.marxist.android.model.QuotesResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubHelper @Inject constructor(private val apiService: GitHubService) {

    suspend fun getBooks(): Response<BooksResponse> = apiService.getBooks()

    suspend fun getQuotes(): Response<QuotesResponse> = apiService.getQuotes()
}