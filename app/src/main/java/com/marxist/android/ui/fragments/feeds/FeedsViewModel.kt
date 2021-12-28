package com.marxist.android.ui.fragments.feeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.data.model.WPPost
import com.marxist.android.data.repository.WordPressRepository
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalFeedsDao
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val repository: WordPressRepository
) : ViewModel() {
    private var feedsDao: LocalFeedsDao = appDatabase.localFeedsDao()

    private var _feedsDownloaded: MutableLiveData<MutableList<LocalFeeds>> = MutableLiveData()
    val feedsDownloaded: MutableLiveData<MutableList<LocalFeeds>> = _feedsDownloaded

    private val _wpPost: MutableLiveData<List<WPPost>> = MutableLiveData()
    val wpPost: LiveData<List<WPPost>> = _wpPost

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _feedsDownloaded.value = feedsDao.getDownloaded(true)
    }

    private var pageNumber = 1
    private var perPage = 20

    fun getFeeds() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPosts(perPage, pageNumber).collect {
                when (it) {
                    is NetworkResponse.Loading -> {
                        _loading.postValue(true)
                    }
                    is NetworkResponse.EmptyResponse -> {
                        _loading.postValue(false)
                    }
                    is NetworkResponse.Success -> {
                        perPage += 20
                        pageNumber += 1
                        _loading.postValue(false)
                        _wpPost.postValue(it.data)
                    }
                    is NetworkResponse.Error -> {
                        _loading.postValue(false)
                        _errorMessage.postValue(it.message)
                    }
                }
            }
        }
    }

    fun getDownloadedFeeds() {
        _feedsDownloaded.value = feedsDao.getDownloaded(true)
    }
}