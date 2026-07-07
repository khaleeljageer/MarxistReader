package org.cpimtn.marxist.android.app

import android.content.Context
import com.jskaleel.epub.EpubApplication
import com.jskaleel.epub.utils.IResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.GetBookFilePathUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBookReaderIdUseCase
import org.cpimtn.marxist.android.domain.usecase.SaveBookReaderIdUseCase
import java.io.File
import javax.inject.Inject

/**
 * Hands a downloaded book off to the epub reader. Lives in :app since it's the only module
 * wired to both the catalog (:use-cases/:domain) and the reader (:reader, via [EpubApplication]).
 * A book is imported into the reader's own DB at most once; the resulting reader id is cached
 * via [saveBookReaderIdUseCase] so re-opening a book reuses its reading progress.
 */
@HiltViewModel
class OpenBookViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBookFilePathUseCase: GetBookFilePathUseCase,
    private val getBookReaderIdUseCase: GetBookReaderIdUseCase,
    private val saveBookReaderIdUseCase: SaveBookReaderIdUseCase,
) : ViewModel() {

    private val eBookReaderRepository get() = (context as EpubApplication).eBookReaderRepository

    private val _readerReady = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val readerReady: SharedFlow<Long> = _readerReady.asSharedFlow()

    fun openBook(bookId: String) {
        viewModelScope.launch {
            val readerId = getBookReaderIdUseCase(bookId) ?: importBook(bookId) ?: return@launch
            val result = eBookReaderRepository.openBook(readerId)
            if (result is IResult.Success) {
                _readerReady.emit(readerId)
            }
        }
    }

    private suspend fun importBook(bookId: String): Long? {
        val filePath = getBookFilePathUseCase(bookId) ?: return null
        val result = eBookReaderRepository.importBook(File(filePath))
        return (result as? IResult.Success)?.id?.also { readerId ->
            saveBookReaderIdUseCase(bookId, readerId)
        }
    }
}
