package org.cpimtn.marxist.android.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Caches the reader-assigned id a downloaded book was given after being imported into the
 * epub reader, keyed by the catalog [bookId]. Kept in its own table (not on [BookEntity]) so
 * it survives catalog re-syncs, which replace all rows in `books`.
 */
@Entity(tableName = "book_reader_links")
data class BookReaderLinkEntity(
    @PrimaryKey
    val bookId: String,
    val readerId: Long,
)
