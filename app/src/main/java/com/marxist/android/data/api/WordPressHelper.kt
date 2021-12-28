package com.marxist.android.data.api

import com.marxist.android.data.model.WPPost
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordPressHelper @Inject constructor(private val apiService: WordPressService) {

    suspend fun getPosts(count: Int, page: Int): Response<List<WPPost>> = apiService.getPosts(count, page)

    suspend fun searchByTerms(key: String, count: Int) = apiService.searchByTerms(key, count)
}