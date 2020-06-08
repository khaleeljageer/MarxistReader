package com.marxist.android.ui.fragments.feeds

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalFeedsDao
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.RssFeed
import com.marxist.android.utils.api.ApiService
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class FeedsViewModel(
    context: Context, appDatabase: AppDatabase,
    private val apiService: ApiService
) : ViewModel() {
    private var feedsDao: LocalFeedsDao = appDatabase.localFeedsDao()
    private var feedsLiveData: LiveData<MutableList<LocalFeeds>>
    private var feedsDownloaded: LiveData<MutableList<LocalFeeds>>

    private val disposable = CompositeDisposable()

    private var _feedList: MutableLiveData<MutableList<LocalFeeds>> = MutableLiveData()
    val feedList: MutableLiveData<MutableList<LocalFeeds>> = _feedList

    init {
        feedsLiveData = feedsDao.getAllFeeds()
        feedsDownloaded = feedsDao.getDownloaded(true)
    }

    fun insert(localBooks: LocalFeeds) {
        feedsDao.insert(localBooks)
    }

    fun getFeeds() {
        disposable.add(
            apiService.getFeeds(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(RetryWithDelay())
                .subscribe({
                    if (it?.channel != null && it.channel!!.itemList != null) {

                    }
                }, {})
        )
    }

    fun getFeedsCount() = feedsDao.getFeedsCount()

    fun getLiveFeeds(): LiveData<MutableList<LocalFeeds>> {
        return feedsLiveData
    }

    fun getDownloadedFeeds(): LiveData<MutableList<LocalFeeds>> {
        return feedsDownloaded
    }

    fun updateFeeds(it: RssFeed?): Boolean {
        return if (it?.channel != null && it.channel!!.itemList != null) {
            val itemList = it.channel!!.itemList
            if (itemList != null && itemList.isNotEmpty()) {
                itemList.forEach { feed ->
                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                    val mDate = simpleDateFormat.parse(feed.pubDate)
                    val timeInMillis = mDate!!.time

                    val localFeeds = LocalFeeds(
                        feed.title!!,
                        feed.link!!,
                        timeInMillis,
//                        feed.description!!,
                        feed.content!!,
                        if (feed.enclosure == null) {
                            ""
                        } else {
                            feed.enclosure!!.audioUrl!!
                        },
                        isDownloaded = false,
                        isBookMarked = false
                    )
                    insert(localFeeds)
                }
            }
            true
        } else {
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}