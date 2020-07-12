package com.marxist.android.ui.activities.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.ConnectivityType
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.base.BaseActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.fragments.feeds.FeedListAdapter
import com.marxist.android.utils.DeviceUtils
import com.marxist.android.utils.PrintLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


class SearchActivity : BaseActivity(), ItemClickListener {
    companion object {
        const val RECOGNIZER_REQ_CODE = 1234
    }

    private val searchViewModel: SearchViewModel by viewModel()
    private val feedAdapter by lazy {
        FeedListAdapter(baseContext, mutableListOf(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            statusBarColor = ContextCompat.getColor(baseContext, R.color.colorSecondaryDark)
        }
        setContentView(R.layout.activity_search)

        ivVoiceSearch.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.marxist.android")
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
            startActivityForResult(
                intent,
                RECOGNIZER_REQ_CODE
            )
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }

        rvListView.setHasFixedSize(true)
        rvListView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvListView.adapter = feedAdapter

        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                DeviceUtils.hideSoftKeyboard(this)
                val key = edtSearch.text?.toString()
                if (key != null && key.isNotEmpty()) {
                    callSearch(key.toString())
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        searchViewModel.searchFeedList.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                feedAdapter.addFeed(it)
            }
            loading = false
        })

        searchViewModel.errorView.observe(this, androidx.lifecycle.Observer {
            displayMaterialSnackBar("No result found", ConnectivityType.OTHER, rootView)
        })

        rvScrollListener()
    }

    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 4
    private var firstVisibleItemPosition = 0
    private var loading: Boolean = false
    private fun rvScrollListener() {
        val layoutManager = rvListView.layoutManager as StaggeredGridLayoutManager
        rvListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                firstVisibleItemPosition =
                    if (layoutManager.childCount > 0) layoutManager.findFirstVisibleItemPositions(
                        null
                    )[0] else 0
                visibleItemCount = layoutManager.childCount
                totalItemCount = layoutManager.itemCount
                if (totalItemCount - visibleItemCount <= firstVisibleItemPosition + visibleThreshold ||
                    totalItemCount == 0
                ) {
                    if (!loading) {
                        loading = true
                        val key = edtSearch.text?.toString() ?: ""
                        if (key.length > 3) {
                            searchViewModel.searchKey = key
                            searchViewModel.search()
                        } else {
                            loading = false
                        }
                    }
                }
            }
        })
    }

    private var disposable: Disposable? = null
    private fun callSearch(key: String) {
        disposable?.dispose()
        disposable = Observable.timer(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                if (key.length > 3) {
                    searchViewModel.resetList()
                    searchViewModel.searchKey = key
                    loading = true
                    searchViewModel.search()
                }
            }
            .subscribe({
            }, {
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RECOGNIZER_REQ_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result: ArrayList<String>? =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (result != null) {
                        edtSearch.text!!.clear()
                        edtSearch.append(result[0])
                        callSearch(result[0])
                    }
                }
            }
        }
    }

    override fun feedItemClickListener(article: Any, adapterPosition: Int, view: View) {
        if (article is LocalFeeds) {
            val intent = Intent(baseContext, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.ARTICLE, article)
            startActivity(intent)
        }
    }
}