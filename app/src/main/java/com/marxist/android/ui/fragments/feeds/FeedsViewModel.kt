package com.marxist.android.ui.fragments.feeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalFeedsDao
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.api.ApiService
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class FeedsViewModel(
    appDatabase: AppDatabase,
    private val apiService: ApiService
) : ViewModel() {
    private var feedsDao: LocalFeedsDao = appDatabase.localFeedsDao()
    private var feedsDownloaded: LiveData<MutableList<LocalFeeds>>

    private val disposable = CompositeDisposable()

    private var _feedList: MutableLiveData<List<LocalFeeds>> = MutableLiveData()
    val feedList: MutableLiveData<List<LocalFeeds>> = _feedList

    init {
        feedsDownloaded = feedsDao.getDownloaded(true)
    }

    private var pageNumber = 1

    fun getFeeds() {
        disposable.add(
            apiService.getFeeds(pageNumber).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(RetryWithDelay())
                .subscribe({
                    if (it?.channel != null && it.channel!!.itemList != null) {
                        val itemList = it.channel!!.itemList
                        if (itemList != null && itemList.isNotEmpty()) {
                            val localFeeds = mutableListOf<LocalFeeds>()
                            itemList.forEach { feed ->
                                val simpleDateFormat =
                                    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                                val mDate = simpleDateFormat.parse(feed.pubDate)
                                val timeInMillis = mDate!!.time
                                val localFeed = LocalFeeds(
                                    feed.title!!,
                                    feed.link!!,
                                    timeInMillis,
                                    feed.content!!,
                                    if (feed.enclosure == null) {
                                        ""
                                    } else {
                                        feed.enclosure!!.audioUrl!!
                                    },
                                    isDownloaded = false
                                )
                                localFeeds.add(localFeed)
                            }
                            _feedList.value = localFeeds

                            pageNumber += 1
                        }
                    }
                }, {})
        )
    }

    fun getDownloadedFeeds(): LiveData<MutableList<LocalFeeds>> {
        return feedsDownloaded
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun resetList() {
        _feedList.value = mutableListOf()
    }
}