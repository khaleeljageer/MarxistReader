package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.AppPreference
import com.marxist.android.utils.AppPreference.get
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.api.ApiClient
import com.marxist.android.utils.api.RetryWithDelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FeedsFragment : Fragment(), ItemClickListener {
    private var pageIndex: Int = 6
    private var feedDisposable: Disposable? = null
    private lateinit var feedAdapter: FeedListAdapter
    private lateinit var mContext: Context
    private val feedsViewModel: FeedsViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        feedAdapter = FeedListAdapter(mContext, mutableListOf(), this@FeedsFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        pageIndex =
            AppPreference.customPrefs(mContext.applicationContext)[AppConstants.SharedPreference.PAGED_INDEX, 6]
    }

    private fun initData() {
        feedsViewModel.getFeeds()
        feedsViewModel.feedList.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                feedAdapter.addFeed(it)
            } else {

            }
        })
/*        feedsViewModel.getLiveFeeds().observe(this, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    rvListView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    feedAdapter.addFeeds(it)
                } else {
                    rvListView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
                feedsViewModel.getLiveFeeds().removeObservers(this)
            }
        })*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragments_list, container, false)

        view.rvListView.setHasFixedSize(true)
//        view.rvListView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        view.rvListView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        view.rvListView.adapter = feedAdapter

        return view
    }

    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 2
    private var loading: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val linearLayoutManager = rvListView.layoutManager as StaggeredGridLayoutManager
        rvListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!loading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    loading = true
                    fetchFeeds()
                }*/
            }
        })
    }


    override fun feedItemClickListener(
        article: Any,
        adapterPosition: Int,
        view: View
    ) {
        if (article is LocalFeeds) {
            val intent = Intent(mContext, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.ARTICLE, article)
            startActivity(intent)
        }
    }

    private fun fetchFeeds() {
        feedAdapter.addLoaderItem()
        feedsViewModel.getLiveFeeds().removeObservers(this)
        feedDisposable = ApiClient.mApiService.getFeeds(pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen(RetryWithDelay())
            .subscribe({
                if (isAdded) {
                    feedAdapter.removeLoaderItem()
                    if (it?.channel != null && it.channel!!.itemList != null) {
                        val itemList = it.channel!!.itemList
                        if (itemList != null && itemList.isNotEmpty()) {
                            val finalList = mutableListOf<LocalFeeds>()
                            itemList.forEach { feed ->
                                val simpleDateFormat =
                                    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                                val mDate = simpleDateFormat.parse(feed.pubDate)
                                val timeInMillis = mDate!!.time

                                val localFeeds = LocalFeeds(
                                    feed.title!!,
                                    feed.link!!,
                                    timeInMillis,
//                                    feed.description!!,
                                    feed.content!!,
                                    if (feed.enclosure == null) {
                                        ""
                                    } else {
                                        feed.enclosure!!.audioUrl!!
                                    },
                                    isDownloaded = false
//                                    isBookMarked = false
                                )
                                finalList.add(localFeeds)
                            }

                            feedAdapter.addFeeds(finalList)
                        }
                        pageIndex++
                    }

                    loading = false
                }
            }, {
                if (isAdded) {
                    feedAdapter.removeLoaderItem()
                    it.printStackTrace()
                    RxBus.publish(ShowSnackBar(getString(R.string.try_later)))
                    loading = false
                }
            })
    }

}