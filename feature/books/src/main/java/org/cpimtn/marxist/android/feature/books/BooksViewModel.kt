package org.cpimtn.marxist.android.feature.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.model.BookDownloadState
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.usecase.DeleteBookDownloadUseCase
import org.cpimtn.marxist.android.domain.usecase.DownloadBookUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBooksFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetDownloadedBookIdsUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncBooksUseCase
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val getBooksFlowUseCase: GetBooksFlowUseCase,
    private val getDownloadedBookIdsUseCase: GetDownloadedBookIdsUseCase,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val deleteBookDownloadUseCase: DeleteBookDownloadUseCase,
    private val syncBooksUseCase: SyncBooksUseCase,
) : ViewModel() {

    private val _booksUiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val booksUiState: StateFlow<BooksUiState> = _booksUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<String, BookDownloadUiState>>(emptyMap())

    init {
        viewModelScope.launch {
            val downloaded = getDownloadedBookIdsUseCase()
            if (downloaded.isNotEmpty()) {
                _downloadStates.update { it + downloaded.associateWith { BookDownloadUiState.Downloaded } }
            }
        }
        viewModelScope.launch {
            combine(
                getBooksFlowUseCase(),
                _downloadStates,
            ) { books, downloadStates ->
                when {
                    books.isEmpty() -> BooksUiState.Empty
                    else -> BooksUiState.Success(books = books, downloadStates = downloadStates)
                }
            }.catch { e ->
                _booksUiState.value = BooksUiState.Error(e.message ?: "Unknown error")
            }.collect { newState ->
                if (_booksUiState.value !is BooksUiState.Error) {
                    _booksUiState.value = newState
                }
            }
        }
    }

    /** Starts downloading [book]'s epub. No-op if already downloading or downloaded. */
    fun downloadBook(book: Book) {
        val current = _downloadStates.value[book.id]
        if (current == BookDownloadUiState.Downloading || current == BookDownloadUiState.Downloaded) return

        viewModelScope.launch {
            downloadBookUseCase(book).collect { state ->
                _downloadStates.update { it + (book.id to state.toUiState()) }
            }
        }
    }

    /** Removes [book]'s downloaded epub, returning the card to its "not downloaded" state. */
    fun deleteBook(book: Book) {
        if (_downloadStates.value[book.id] != BookDownloadUiState.Downloaded) return

        viewModelScope.launch {
            deleteBookDownloadUseCase(book.id)
            _downloadStates.update { it + (book.id to BookDownloadUiState.NotDownloaded) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                when (val result = syncBooksUseCase()) {
                    is SyncResult.Success -> {
                        if (_booksUiState.value is BooksUiState.Error) {
                            _booksUiState.value = BooksUiState.Loading
                        }
                    }

                    is SyncResult.NetworkError ->
                        _booksUiState.value = BooksUiState.Error(result.message ?: "Network error")

                    is SyncResult.ServerError ->
                        _booksUiState.value = BooksUiState.Error(result.message ?: "Server error")

                    is SyncResult.UnknownError ->
                        _booksUiState.value =
                            BooksUiState.Error(result.cause?.message ?: "Something went wrong")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

sealed interface BooksUiState {
    data object Loading : BooksUiState
    data class Success(
        val books: List<Book>,
        val downloadStates: Map<String, BookDownloadUiState>,
    ) : BooksUiState

    data object Empty : BooksUiState
    data class Error(val message: String) : BooksUiState
}

enum class BookDownloadUiState { NotDownloaded, Downloading, Downloaded, Failed }

private fun BookDownloadState.toUiState(): BookDownloadUiState = when (this) {
    is BookDownloadState.Downloading -> BookDownloadUiState.Downloading
    is BookDownloadState.Downloaded -> BookDownloadUiState.Downloaded
    is BookDownloadState.Failed -> BookDownloadUiState.Failed
}

enum class StateKey { Loading, Content, Empty, Error }

val BooksUiState.stateKey: StateKey
    get() = when (this) {
        is BooksUiState.Loading -> StateKey.Loading
        is BooksUiState.Success -> StateKey.Content
        is BooksUiState.Empty -> StateKey.Empty
        is BooksUiState.Error -> StateKey.Error
    }
