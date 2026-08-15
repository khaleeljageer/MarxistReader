package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.SavedPostDao
import org.cpimtn.marxist.android.data.source.local.database.entity.SavedPostEntity
import org.cpimtn.marxist.android.domain.repository.SavedPostRepository
import javax.inject.Inject

class SavedPostRepositoryImpl @Inject constructor(
    private val savedPostDao: SavedPostDao,
) : SavedPostRepository {

    override fun getSavedPostIds(): Flow<Set<Int>> =
        savedPostDao.getAllSavedPostIds().map { it.toSet() }

    override suspend fun save(postId: Int) {
        savedPostDao.insert(SavedPostEntity(postId = postId))
    }

    override suspend fun unsave(postId: Int) {
        savedPostDao.deleteByPostId(postId)
    }
}
