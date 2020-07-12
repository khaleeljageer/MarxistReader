package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.activities.search.SearchActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.PrintLog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragments_list.*
import kotlinx.android.synthetic.main.fragments_list.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FeedsFragment : Fragment(), ItemClickListener {
    private var feedDisposable: Disposable? = null
    private val feedAdapter by lazy {
        FeedListAdapter(mContext, mutableListOf(), this@FeedsFragment)
    }
    private lateinit var mContext: Context
    private val feedsViewModel: FeedsViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        feedsViewModel.getFeeds()
        feedsViewModel.feedList.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                feedAdapter.addFeed(it)
                loading = false
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
        view.rvListView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        view.rvListView.adapter = feedAdapter

        return view
    }

    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 4
    private var firstVisibleItemPosition = 0
    private var loading: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PrintLog.debug("Khaleel", "onActivityCreated")
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
                        feedsViewModel.getFeeds()
                    }
                }
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
}