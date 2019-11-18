package com.marxist.android.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalFeedsDao
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.RssFeed
import java.text.SimpleDateFormat
import java.util.*

class FeedsViewModel(application: Application) : AndroidViewModel(application) {
    private var feedsDao: LocalFeedsDao = AppDatabase.getAppDatabase(application).localFeedsDao()
    private var feedsLiveData: LiveData<MutableList<LocalFeeds>>
    private var feedsDownloaded: LiveData<MutableList<LocalFeeds>>

    init {
        feedsLiveData = feedsDao.getAllFeeds()
        feedsDownloaded = feedsDao.getDownloaded(true)
    }

    fun insert(localBooks: LocalFeeds) {
        feedsDao.insert(localBooks)
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
                        feed.description!!,
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
}