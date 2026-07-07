package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.BookRepository

class SaveBookReaderIdUseCase(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String, readerId: Long) =
        bookRepository.saveReaderId(bookId, readerId)
}
