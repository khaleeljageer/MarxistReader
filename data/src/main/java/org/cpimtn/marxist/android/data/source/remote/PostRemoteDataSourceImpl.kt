package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.api.WPApiService
import org.cpimtn.marxist.network.model.PostDTO
import javax.inject.Inject

/**
 * Fetches posts from the API. Implementation of PostRemoteDataSource (DIP).
 */
class PostRemoteDataSourceImpl @Inject constructor(
    private val apiService: WPApiService,
) : PostRemoteDataSource {

    override suspend fun fetchPage(page: Int, perPage: Int): List<PostDTO>? {
        val response = apiService.getPosts(page = page, perPage = perPage)
        if (!response.isSuccessful) return null
        return response.body()
    }
}
