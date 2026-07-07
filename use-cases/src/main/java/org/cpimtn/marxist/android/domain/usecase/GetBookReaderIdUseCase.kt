package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.BookRepository

class GetBookReaderIdUseCase(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String): Long? = bookRepository.getReaderId(bookId)
}
