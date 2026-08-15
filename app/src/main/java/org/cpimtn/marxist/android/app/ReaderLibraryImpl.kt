package org.cpimtn.marxist.android.app

import android.content.Context
import com.jskaleel.epub.EpubApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import org.cpimtn.marxist.android.domain.repository.ReaderLibrary
import javax.inject.Inject

/**
 * Implements the [ReaderLibrary] port against the epub reader's own repository. Lives in `:app`
 * for the same reason [OpenBookViewModel] does — it is the only module wired to both the catalog
 * and `:reader`.
 */
class ReaderLibraryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReaderLibrary {

    override suspend fun removeBook(readerId: Long) {
        (context as EpubApplication).eBookReaderRepository.deleteBook(readerId)
    }
}
