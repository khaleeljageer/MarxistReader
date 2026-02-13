package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.api.WPApiService
import org.cpimtn.marxist.network.model.CategoryDTO
import javax.inject.Inject

class CategoryRemoteDataSourceImpl @Inject constructor(
    private val apiService: WPApiService,
) : CategoryRemoteDataSource {

    override suspend fun fetchPage(page: Int, perPage: Int): List<CategoryDTO>? {
        val response = apiService.getCategories(perPage = perPage, page = page)
        if (!response.isSuccessful) return null
        return response.body()
    }
}
