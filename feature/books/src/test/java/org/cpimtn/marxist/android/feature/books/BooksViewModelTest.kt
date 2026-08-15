package org.cpimtn.marxist.android.feature.books

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.usecase.DeleteBookDownloadUseCase
import org.cpimtn.marxist.android.domain.usecase.DownloadBookUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBooksFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetDownloadedBookIdsUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncBooksUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val book = Book(
        id = "book-1",
        title = "Capital",
        date = "Jan, 2022",
        imageUrl = "https://example.invalid/cover.jpg",
        epubUrl = "https://example.invalid/book.epub",
    )

    private val getBooksFlowUseCase: GetBooksFlowUseCase = mockk {
        every { this@mockk.invoke() } returns flowOf(listOf(book))
    }
    private val getDownloadedBookIdsUseCase: GetDownloadedBookIdsUseCase = mockk {
        coEvery { this@mockk.invoke() } returns setOf(book.id)
    }
    private val downloadBookUseCase: DownloadBookUseCase = mockk()
    private val deleteBookDownloadUseCase: DeleteBookDownloadUseCase = mockk {
        coEvery { this@mockk.invoke(any()) } returns Unit
    }
    private val syncBooksUseCase: SyncBooksUseCase = mockk()

    private fun viewModel() = BooksViewModel(
        getBooksFlowUseCase = getBooksFlowUseCase,
        getDownloadedBookIdsUseCase = getDownloadedBookIdsUseCase,
        downloadBookUseCase = downloadBookUseCase,
        deleteBookDownloadUseCase = deleteBookDownloadUseCase,
        syncBooksUseCase = syncBooksUseCase,
    )

    private fun successState(state: BooksUiState) =
        state as? BooksUiState.Success ?: error("expected Success but was $state")

    @Test
    fun downloaded_books_start_in_downloaded_state() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals(
            BookDownloadUiState.Downloaded,
            successState(viewModel.booksUiState.value).downloadStates[book.id],
        )
    }

    @Test
    fun deleteBook_flips_state_to_not_downloaded_without_a_refresh() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.deleteBook(book)
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteBookDownloadUseCase(book.id) }
        // The card has to return to "download" off the back of this state alone — nothing else
        // re-reads what is on disk, so a stale value here would only clear on a manual refresh.
        assertEquals(
            BookDownloadUiState.NotDownloaded,
            successState(viewModel.booksUiState.value).downloadStates[book.id],
        )
    }

    @Test
    fun deleteBook_is_a_no_op_for_a_book_that_is_not_downloaded() = runTest {
        coEvery { getDownloadedBookIdsUseCase() } returns emptySet()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.deleteBook(book)
        advanceUntilIdle()

        coVerify(exactly = 0) { deleteBookDownloadUseCase(any()) }
    }
}
