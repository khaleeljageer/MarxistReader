package com.marxist.android.data.repository

import com.marxist.android.data.api.WordPressHelper
import com.marxist.android.data.model.WPPost
import com.marxist.android.utils.network.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordPressRepository @Inject constructor(private val wordPressHelper: WordPressHelper) {

    suspend fun getPosts(count: Int, page: Int): Flow<NetworkResponse<List<WPPost>>> = flow {
        emit(NetworkResponse.Loading)
        val response = wordPressHelper.getPosts(count, page)
        if (response.isSuccessful) {
            val list: List<WPPost>? = response.body()
            if (list != null && list.isNotEmpty()) {
                list.forEach {
                    it.apply {
                        val jsoup: Document = Jsoup.parse(it.content.rendered)
                        val audioTag = jsoup.getElementsByTag("audio").first()
                        if (audioTag != null) {
                            this.audioUrl = audioTag.absUrl("src")
                        } else {
                            this.audioUrl = ""
                        }
                    }
                }
                emit(NetworkResponse.Success(list))
            } else {
                emit(NetworkResponse.EmptyResponse)
            }
        } else {
            emit(NetworkResponse.Error(response.message() ?: "Unknown error"))
        }
    }
}