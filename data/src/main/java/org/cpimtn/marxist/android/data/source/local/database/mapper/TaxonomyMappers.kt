package org.cpimtn.marxist.android.data.source.local.database.mapper

import org.cpimtn.marxist.android.data.source.local.database.entity.CategoryEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.TagEntity
import org.cpimtn.marxist.android.domain.model.Category
import org.cpimtn.marxist.android.domain.model.Tag
import org.cpimtn.marxist.network.model.CategoryDTO
import org.cpimtn.marxist.network.model.TagDTO

fun CategoryDTO.toEntity(): CategoryEntity = CategoryEntity(id = id, name = name)
fun CategoryEntity.toDomain(): Category = Category(id = id, name = name)

fun TagDTO.toEntity(): TagEntity = TagEntity(id = id, name = name)
fun TagEntity.toDomain(): Tag = Tag(id = id, name = name)
