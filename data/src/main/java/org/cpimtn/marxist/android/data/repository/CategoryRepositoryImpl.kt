package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.data.source.local.database.dao.CategoryDao
import org.cpimtn.marxist.android.data.source.local.database.entity.CategoryEntity
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.data.source.local.database.mapper.toEntity
import org.cpimtn.marxist.android.data.source.remote.CategoryRemoteDataSource
import org.cpimtn.marxist.android.domain.model.Category
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val remoteDataSource: CategoryRemoteDataSource,
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        categoryDao.getAll().map { it.map(CategoryEntity::toDomain) }

    override suspend fun syncCategories(): Result<Unit> = runCatching {
        val perPage = 100
        val all = mutableListOf<CategoryEntity>()
        var page = 1
        while (true) {
            val categoryDTOS = remoteDataSource.fetchPage(page = page, perPage = perPage) ?: break
            if (categoryDTOS.isEmpty()) break
            all.addAll(categoryDTOS.map { it.toEntity() })
            if (categoryDTOS.size < perPage) break
            page++
        }
        if (all.isNotEmpty()) categoryDao.replaceAll(all)
    }
}
