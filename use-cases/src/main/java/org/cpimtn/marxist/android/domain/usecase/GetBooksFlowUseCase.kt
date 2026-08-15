package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.repository.BookRepository

class GetBooksFlowUseCase(
    private val bookRepository: BookRepository,
) {
    operator fun invoke(): Flow<List<Book>> = bookRepository.getBooks()
}
