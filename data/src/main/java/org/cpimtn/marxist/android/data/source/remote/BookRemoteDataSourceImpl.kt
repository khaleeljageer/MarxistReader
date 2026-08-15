package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.core.config.AppConfig
import org.cpimtn.marxist.network.api.BooksApiService
import org.cpimtn.marxist.network.model.BookDTO
import javax.inject.Inject

/**
 * Fetches the books catalog from GitHub. Implementation of BookRemoteDataSource (DIP).
 */
class BookRemoteDataSourceImpl @Inject constructor(
    private val apiService: BooksApiService,
) : BookRemoteDataSource {

    override suspend fun fetchBooks(): List<BookDTO>? {
        val response = apiService.getBooks(AppConfig.Books.CATALOG_URL)
        if (!response.isSuccessful) return null
        return response.body()?.books
    }
}
