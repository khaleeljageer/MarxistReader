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

    private var _feedList: MutableLiveData<List<LocalFeeds>> = MutableLiveData()
    val feedList: MutableLiveData<List<LocalFeeds>> = _feedList

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

    fun getFeeds(count: Int = 20, page: Int = 1) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPosts(count, page).collect {
                when (it) {
                    is NetworkResponse.Loading -> {
                        _loading.postValue(true)
                    }
                    is NetworkResponse.EmptyResponse -> {
                        _loading.postValue(false)
                    }
                    is NetworkResponse.Success -> {
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
//        disposable.add(
//            apiService.getFeeds(pageNumber).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .retryWhen(RetryWithDelay())
//                .subscribe({
//                    if (it?.channel != null && it.channel!!.itemList != null) {
//                        val itemList = it.channel!!.itemList
//                        if (itemList != null && itemList.isNotEmpty()) {
//                            val localFeeds = mutableListOf<LocalFeeds>()
//                            itemList.forEach { feed ->
//                                val simpleDateFormat =
//                                    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
//                                val mDate = simpleDateFormat.parse(feed.pubDate)
//                                val timeInMillis = mDate!!.time
//                                val localFeed = LocalFeeds(
//                                    feed.title!!,
//                                    feed.link!!,
//                                    timeInMillis,
//                                    feed.content!!,
//                                    if (feed.enclosure == null) {
//                                        ""
//                                    } else {
//                                        feed.enclosure!!.audioUrl!!
//                                    },
//                                    isDownloaded = false
//                                )
//                                localFeeds.add(localFeed)
//                            }
//                            _feedList.value = localFeeds
//
//                            pageNumber += 1
//                        }
//                    }
//                }, {})
//        )
    }

    fun getDownloadedFeeds() {
        _feedsDownloaded.value = feedsDao.getDownloaded(true)
    }

    fun resetList() {
        _feedList.value = mutableListOf()
    }
}