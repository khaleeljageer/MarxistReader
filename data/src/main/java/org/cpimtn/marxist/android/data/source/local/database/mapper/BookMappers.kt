package org.cpimtn.marxist.android.data.source.local.database.mapper

import org.cpimtn.marxist.android.data.source.local.database.entity.BookEntity
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.network.model.BookDTO

/** Maps DTO -> Entity (for persistence). [position] preserves catalog order. */
fun BookDTO.toEntity(position: Int): BookEntity = BookEntity(
    id = bookid,
    title = title.trim(),
    date = date.trim(),
    imageUrl = image,
    epubUrl = epub,
    position = position,
)

/** Maps Entity -> Domain (for consumers). */
fun BookEntity.toDomain(): Book = Book(
    id = id,
    title = title,
    date = date,
    imageUrl = imageUrl,
    epubUrl = epubUrl,
)
