package org.cpimtn.marxist.android.data.source.remote

import org.cpimtn.marxist.network.model.TagDTO

interface TagRemoteDataSource {
    suspend fun fetchPage(page: Int, perPage: Int): List<TagDTO>?
}
