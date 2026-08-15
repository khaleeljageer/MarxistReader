package org.cpimtn.marxist.network.api

import org.cpimtn.marxist.network.model.BooksResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Books catalog lives on a separate host (GitHub raw) from the WordPress API,
 * so the full URL is passed per-call instead of using the shared Retrofit base URL.
 */
interface BooksApiService {
    @GET
    suspend fun getBooks(@Url url: String): Response<BooksResponseDTO>
}
