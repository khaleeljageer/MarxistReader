package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.model.BookDTO

/**
 * Abstraction for fetching the books catalog from the network. Repository depends on this (DIP).
 */
interface BookRemoteDataSource {
    /** Fetches the full books catalog. Returns null on failure or empty body. */
    suspend fun fetchBooks(): List<BookDTO>?
}
