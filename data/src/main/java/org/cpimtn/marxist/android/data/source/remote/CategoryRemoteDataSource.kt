package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.model.CategoryDTO

interface CategoryRemoteDataSource {
    suspend fun fetchPage(page: Int, perPage: Int): List<CategoryDTO>?
}
