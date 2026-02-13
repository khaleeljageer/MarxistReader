package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Tag

interface TagRepository {
    fun getTags(): Flow<List<Tag>>
    suspend fun syncTags(): Result<Unit>
}
