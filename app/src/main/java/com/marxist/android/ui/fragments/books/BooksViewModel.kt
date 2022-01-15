package com.marxist.android.ui.fragments.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.data.repository.GitHubRepository
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalBooksDao
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    appDatabase: AppDatabase,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    private var booksDao: LocalBooksDao = appDatabase.localBooksDao()
    private val _booksLiveData: MutableLiveData<MutableList<LocalBooks>> =
        MutableLiveData<MutableList<LocalBooks>>()
    var booksLiveData: LiveData<MutableList<LocalBooks>> = _booksLiveData

    private fun insert(highlights: LocalBooks) {
        booksDao.insert(highlights)
    }

    fun getLocalBooksSize(): Int {
        return booksDao.getAllBooks().size
    }

    fun callBookApi() {
        viewModelScope.launch(Dispatchers.IO) {
            gitHubRepository.getBooks().collect {
                when (it) {
                    is NetworkResponse.Success -> {
                        _booksLiveData.postValue(it.data.books)
                    }
                    is NetworkResponse.Error -> {

                    }
                    is NetworkResponse.EmptyResponse -> {

                    }
                    NetworkResponse.Loading -> {

                    }
                }
            }
        }
    }
}