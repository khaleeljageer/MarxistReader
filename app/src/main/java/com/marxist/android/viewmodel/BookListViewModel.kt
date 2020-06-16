package com.marxist.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalBooksDao
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.utils.api.GitHubService
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class BookListViewModel(
    appDatabase: AppDatabase,
    private val apiService: GitHubService
) : ViewModel() {

    private var booksDao: LocalBooksDao = appDatabase.localBooksDao()
    private var booksLiveData: LiveData<MutableList<LocalBooks>>
    private var disposable: Disposable? = null

    init {
        booksLiveData = booksDao.getAllLocalBooks()
    }

    fun insert(highlights: LocalBooks) {
        booksDao.insert(highlights)
    }

    fun updateStatus(filePath: String, status: Boolean, bookId: String) {
        booksDao.updateDownloadDetails(filePath, status, bookId)
    }

    fun getLocalBooksSize(): Int {
        return booksDao.getAllBooks().size
    }

    fun getLocalBooks(): LiveData<MutableList<LocalBooks>> {
        return booksLiveData
    }

    fun callBookApi() {
        disposable = apiService.getBooks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(RetryWithDelay())
            .subscribe({
                if (it != null) {
                    if (it.books.isNotEmpty()) {
                        for (book in it.books) {
                            if (!booksDao.isIdAvailable(book.bookid)) {
                                val simpleDateFormat =
                                    SimpleDateFormat("MMM, yyyy", Locale.US)
                                val mDate = simpleDateFormat.parse(book.date)
                                val timeInMillis = mDate!!.time

                                book.downloadId = -1
                                book.isDownloaded = false
                                book.savedPath = ""
                                book.pubDate = timeInMillis
                                insert(book)
                            }
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    fun deleteBook(books: LocalBooks) {
        booksDao.deleteBook(books)
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}