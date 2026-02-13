package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Tag
import org.cpimtn.marxist.android.domain.repository.TagRepository

class GetTagsFlowUseCase(
    private val tagRepository: TagRepository,
) {
    operator fun invoke(): Flow<List<Tag>> = tagRepository.getTags()
}
