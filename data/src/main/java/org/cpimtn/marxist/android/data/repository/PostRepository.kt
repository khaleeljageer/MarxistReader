package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Post

interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun fullSync(perPage: Int)
}
