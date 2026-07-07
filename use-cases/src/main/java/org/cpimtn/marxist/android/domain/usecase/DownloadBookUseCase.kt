package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.model.BookDownloadState
import org.cpimtn.marxist.android.domain.repository.BookRepository

class DownloadBookUseCase(
    private val bookRepository: BookRepository,
) {
    operator fun invoke(book: Book): Flow<BookDownloadState> = bookRepository.downloadBook(book)
}
