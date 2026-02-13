package org.cpimtn.marxist.android.data.repository

import com.jskaleel.android.network.api.WPApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.database.dao.PostDao
import org.cpimtn.marxist.android.data.database.entity.toDomain
import org.cpimtn.marxist.android.data.database.entity.toEntity
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Offline First: getPosts() reads from local DB; fullSync() fetches from API and writes to DB.
 */
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: WPApiService,
) : PostRepository {

    override fun getPosts(): Flow<List<Post>> =
        postDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun fullSync(perPage: Int) {
        postDao.clearAll()
        var page = 1
        while (true) {
            val response = apiService.getPosts(page = page, perPage = perPage)
            if (!response.isSuccessful) break
            val body = response.body() ?: break
            if (body.isEmpty()) break
            postDao.insertAll(body.map { it.toEntity() })
            if (body.size < perPage) break
            page++
        }
    }
}
