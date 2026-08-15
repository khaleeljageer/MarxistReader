package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.BookRepository

class GetBookFilePathUseCase(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String): String? = bookRepository.getDownloadedFilePath(bookId)
}
