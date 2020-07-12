package com.marxist.android.ui.activities.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.api.ApiService
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class SearchViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private var _errorView: MutableLiveData<Boolean> = MutableLiveData()
    val errorView: MutableLiveData<Boolean> = _errorView

    private var _searchFeedList: MutableLiveData<List<LocalFeeds>> = MutableLiveData()
    val searchFeedList: MutableLiveData<List<LocalFeeds>> = _searchFeedList

    private var disposable: Disposable? = null
    private var page = 1
    var searchKey: String = ""

    fun search() {
        if (searchKey.isEmpty()) return
        disposable = apiService.search(searchKey, page).subscribeOn(Schedulers.io())
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
                        _searchFeedList.value = localFeeds
                        page += 1
                    }
                }
            }, {
                _errorView.value = true
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun resetList() {
        _searchFeedList.value = mutableListOf()
    }
}