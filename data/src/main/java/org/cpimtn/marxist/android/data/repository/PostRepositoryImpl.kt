package org.cpimtn.marxist.android.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.cpimtn.marxist.android.data.worker.Sync
import org.cpimtn.marxist.android.domain.model.Post
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
//    private val apiService: WPApiService,
//    private val postDao: PostDao,
    @ApplicationContext private val context: Context,
) : PostRepository {
    override fun getPosts(): Flow<List<Post>> {
//        return postDao.getAll().map { entities ->
//            entities.map { it.toDomain() }
//        }

        Sync.initialize(context)

        return emptyFlow()
    }

    override suspend fun fullSync(perPage: Int) {
//        postDao.clearAll()

//        val page1 = apiService.getPageInfo(page = 1, perPage = 1)
//        val totalPages = page1.headers()["X-WP-TotalPages"]?.toIntOrNull() ?: 1
//
//        for (page in 1..totalPages) {
//            val response = apiService.getPosts(page = page, perPage = perPage)
//            if (response.isSuccessful) {
//                val posts = response.body() ?: emptyList()
//                posts.forEach { post ->
//                    Log.d("Khaleel", "Posts: ${post.title.rendered}")
//                }
//                postDao.insertAll(posts.map { it.toEntity() })
//            } else {
//                throw IllegalStateException("Page $page failed with ${response.code()}")
//            }
//        }
    }
}
