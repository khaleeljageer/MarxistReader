package org.cpimtn.marxist.android.domain.model

/**
 * Progress of downloading a book's epub file to local storage. Emitted by
 * [org.cpimtn.marxist.android.domain.repository.BookRepository.downloadBook].
 */
sealed interface BookDownloadState {
    data object Downloading : BookDownloadState
    data class Downloaded(val filePath: String) : BookDownloadState
    data class Failed(val message: String) : BookDownloadState
}
