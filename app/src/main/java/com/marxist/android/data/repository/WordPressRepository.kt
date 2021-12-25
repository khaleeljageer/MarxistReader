package com.marxist.android.data.repository

import com.marxist.android.data.api.WordPressHelper
import com.marxist.android.data.model.WPPost
import com.marxist.android.utils.network.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordPressRepository @Inject constructor(private val wordPressHelper: WordPressHelper) {

    suspend fun getPosts(count: Int, page: Int): Flow<NetworkResponse<List<WPPost>>> = flow {
        val response = wordPressHelper.getPosts(count, page)
        if (response.isSuccessful) {
            val list = response.body()
            if (list != null && list.isNotEmpty()) {
                emit(NetworkResponse.Success(list))
            } else {
                emit(NetworkResponse.EmptyResponse)
            }
        } else {
            emit(NetworkResponse.Error(response.message() ?: "Unknown error"))
        }
    }
}