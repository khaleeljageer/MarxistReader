package org.cpimtn.marxist.android.data.database.mapper

import org.cpimtn.marxist.android.data.database.entity.PostEntity
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.network.model.PostDTO

/**
 * Maps DTO → Entity (for persistence). Single responsibility: mapping from network shape to DB shape.
 */
fun PostDTO.toEntity(): PostEntity = PostEntity(
    id = id,
    date = date,
    slug = slug,
    title = title.rendered,
    excerpt = excerpt.rendered,
    tagsId = tags ?: emptyList(),
    categoriesId = categories ?: emptyList()
)

/**
 * Maps Entity → Domain (for consumers). Single responsibility: mapping from DB shape to domain model.
 */
fun PostEntity.toDomain(): Post = Post(
    id = id,
    date = date,
    slug = slug,
    title = title,
    excerpt = excerpt,
    tags = tagsId,
    categories = categoriesId
)
