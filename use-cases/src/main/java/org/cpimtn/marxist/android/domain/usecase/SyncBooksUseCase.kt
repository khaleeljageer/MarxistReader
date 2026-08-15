package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.BookRepository

class SyncBooksUseCase(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(): SyncResult = bookRepository.fullSync()
}
