package org.cpimtn.marxist.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BooksResponseDTO(
    val books: List<BookDTO>,
)

@Serializable
data class BookDTO(
    val title: String,
    val date: String,
    val bookid: String,
    val image: String,
    val epub: String,
)
