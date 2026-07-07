package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.model.BookDownloadState
import org.cpimtn.marxist.android.domain.model.SyncResult

/**
 * Domain contract for the books catalog (Clean Architecture).
 * Offline First: consumers read the catalog from local cache; sync with the
 * remote catalog is done separately. Downloading an epub is a distinct action
 * from syncing the catalog metadata.
 */
interface BookRepository {
    /** Stream of books from local DB (source of truth). */
    fun getBooks(): Flow<List<Book>>

    /** Runs a full sync of the catalog from the remote source and writes to local DB. */
    suspend fun fullSync(): SyncResult

    /** Downloads [book]'s epub file to local storage, emitting progress until it completes or fails. */
    fun downloadBook(book: Book): Flow<BookDownloadState>

    /** IDs of books whose epub file already exists in local storage. */
    suspend fun getDownloadedBookIds(): Set<String>
}
