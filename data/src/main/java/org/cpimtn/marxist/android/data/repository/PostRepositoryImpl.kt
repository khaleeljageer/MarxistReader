package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.PostDao
import org.cpimtn.marxist.android.data.source.local.database.entity.PostEntity
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.data.source.local.database.mapper.toEntity
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSource
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.PostRepository
import java.io.IOException
import javax.inject.Inject

/**
 * Offline First: getPosts() reads from local DB; fullSync() fetches from remote,
 * then replaces DB in one transaction so a failed sync never wipes data.
 */
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val remoteDataSource: PostRemoteDataSource,
) : PostRepository {

    override fun getPosts(): Flow<List<Post>> =
        postDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun fullSync(perPage: Int): SyncResult = try {
        val allEntities = mutableListOf<PostEntity>()
        var page = 1
        while (true) {
            val pageData = remoteDataSource.fetchPage(page = page, perPage = perPage)
            when {
                pageData == null -> return SyncResult.ServerError(
                    code = -1,
                    message = "Request failed or empty response"
                )

                pageData.isEmpty() -> break
                else -> {
                    allEntities.addAll(pageData.map { it.toEntity() })
                    if (pageData.size < perPage) break
                    page++
                }
            }
        }
        if (allEntities.isNotEmpty()) postDao.replaceAll(allEntities)
        SyncResult.Success
    } catch (e: IOException) {
        SyncResult.NetworkError(e.message)
    } catch (e: Exception) {
        SyncResult.UnknownError(e)
    }

    override fun getPostById(postId: Int): Flow<Post?> {
        return postDao.getPostById(postId).map {
            it?.toDomain()
        }
    }
}
