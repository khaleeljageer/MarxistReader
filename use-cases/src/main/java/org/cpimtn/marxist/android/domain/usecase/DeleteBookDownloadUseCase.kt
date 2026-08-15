package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.repository.BookRepository
import org.cpimtn.marxist.android.domain.repository.ReaderLibrary

/**
 * Removes a downloaded book from the device.
 *
 * The reader imports books by absolute path and keeps its own row plus a cover file, so dropping
 * only the epub would leave it pointing at a missing file. The reader entry goes first: if that
 * fails the epub survives and the book still opens, whereas the reverse order would strand a
 * reader row that can never be reached again.
 */
class DeleteBookDownloadUseCase(
    private val bookRepository: BookRepository,
    private val readerLibrary: ReaderLibrary,
) {
    suspend operator fun invoke(bookId: String) {
        bookRepository.getReaderId(bookId)?.let { readerLibrary.removeBook(it) }
        bookRepository.deleteDownload(bookId)
    }
}
