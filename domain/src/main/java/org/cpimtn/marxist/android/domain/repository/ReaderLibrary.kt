package org.cpimtn.marxist.android.domain.repository

/**
 * Seam onto the epub reader's own library, which keeps a separate database keyed by its own ids.
 * Implemented in `:app` — the only module wired to both the catalog and `:reader` — so use cases
 * can clean up after a book without any module below `:app` knowing the reader exists.
 */
interface ReaderLibrary {
    /** Drops [readerId] from the reader's library, discarding its stored reading progress. */
    suspend fun removeBook(readerId: Long)
}
