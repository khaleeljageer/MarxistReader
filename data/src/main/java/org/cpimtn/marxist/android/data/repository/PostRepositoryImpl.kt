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

    /**
     * On a cold start (empty DB) each page is written as soon as it arrives, so the UI has
     * something to show after a single request instead of waiting for the whole catalog — this is
     * what lets the welcome screen's "continue" gate open on a slow connection. There is no
     * existing data to protect in that case, and a sync that dies half way leaves partial data
     * rather than none.
     *
     * Once the DB is populated, the atomic "fetch fully, then replace in one transaction" rule
     * applies again so a failed refresh never shrinks or wipes what the user already has.
     */
    override suspend fun fullSync(perPage: Int): SyncResult = try {
        val isColdStart = postDao.count() == 0
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
                    val entities = pageData.map { it.toEntity() }
                    if (isColdStart) postDao.insertAll(entities) else allEntities.addAll(entities)
                    if (pageData.size < perPage) break
                    page++
                }
            }
        }
        if (!isColdStart && allEntities.isNotEmpty()) postDao.replaceAll(allEntities)
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
