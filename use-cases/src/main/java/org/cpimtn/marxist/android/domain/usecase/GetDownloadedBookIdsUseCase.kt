package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.BookRepository

class GetDownloadedBookIdsUseCase(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(): Set<String> = bookRepository.getDownloadedBookIds()
}
