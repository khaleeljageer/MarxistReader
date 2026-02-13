package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.api.WPApiService
import org.cpimtn.marxist.network.model.TagDTO
import javax.inject.Inject

class TagRemoteDataSourceImpl @Inject constructor(
    private val apiService: WPApiService,
) : TagRemoteDataSource {

    override suspend fun fetchPage(page: Int, perPage: Int): List<TagDTO>? {
        val response = apiService.getTags(perPage = perPage, page = page)
        if (!response.isSuccessful) return null
        return response.body()
    }
}
