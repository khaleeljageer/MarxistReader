package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.TagDao
import org.cpimtn.marxist.android.data.source.local.database.entity.TagEntity
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.data.source.local.database.mapper.toEntity
import org.cpimtn.marxist.android.data.source.remote.TagRemoteDataSource
import org.cpimtn.marxist.android.domain.model.Tag
import org.cpimtn.marxist.android.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val remoteDataSource: TagRemoteDataSource,
) : TagRepository {

    override fun getTags(): Flow<List<Tag>> =
        tagDao.getAll().map { it.map(TagEntity::toDomain) }

    override suspend fun syncTags(): Result<Unit> = runCatching {
        val perPage = 100
        val all = mutableListOf<TagEntity>()
        var page = 1
        while (true) {
            val tagDTOS = remoteDataSource.fetchPage(page = page, perPage = perPage) ?: break
            if (tagDTOS.isEmpty()) break
            all.addAll(tagDTOS.map { it.toEntity() })
            if (tagDTOS.size < perPage) break
            page++
        }
        if (all.isNotEmpty()) tagDao.replaceAll(all)
    }
}
