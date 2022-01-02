package com.marxist.android.data.repository

import com.marxist.android.data.api.GitHubHelper
import com.marxist.android.model.BooksResponse
import com.marxist.android.model.QuotesResponse
import com.marxist.android.utils.network.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(private val gitHubHelper: GitHubHelper) {

    suspend fun getBooks(): Flow<NetworkResponse<BooksResponse>> = flow {
        val response = gitHubHelper.getBooks()
        if (response.isSuccessful) {
            val list: BooksResponse? = response.body()
            if (list != null && list.books.isNotEmpty()) {
                emit(NetworkResponse.Success(list))
            } else {
                emit(NetworkResponse.EmptyResponse)
            }
        } else {
            emit(NetworkResponse.Error(response.message() ?: "Unknown error"))
        }
    }

    suspend fun getQuotes(): Flow<NetworkResponse<QuotesResponse>> = flow {
        val response = gitHubHelper.getQuotes()
        if (response.isSuccessful) {
            val list: QuotesResponse? = response.body()
            if (list != null && list.quotes.isNotEmpty()) {
                emit(NetworkResponse.Success(list))
            } else {
                emit(NetworkResponse.EmptyResponse)
            }
        } else {
            emit(NetworkResponse.Error(response.message() ?: "Unknown error"))
        }
    }
}