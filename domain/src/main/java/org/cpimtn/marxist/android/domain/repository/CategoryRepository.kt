package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Category

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun syncCategories(): Result<Unit>
}
