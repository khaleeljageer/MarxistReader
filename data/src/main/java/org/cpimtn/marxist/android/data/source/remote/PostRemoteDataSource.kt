package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.model.PostDTO

/**
 * Abstraction for fetching posts from the network. Repository depends on this (DIP).
 */
interface PostRemoteDataSource {
    /**
     * Fetches one page of posts. Returns null on failure or empty body.
     */
    suspend fun fetchPage(page: Int, perPage: Int): List<PostDTO>?
}
