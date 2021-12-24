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
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.activities.search.SearchActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.viewBinding
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedsFragment : Fragment(R.layout.fragments_list), ItemClickListener {
    private var feedDisposable: Disposable? = null
    private val binding by viewBinding(FragmentsListBinding::bind)
    private val feedAdapter by lazy {
        FeedListAdapter(mContext, mutableListOf(), this@FeedsFragment)
    }
    private lateinit var mContext: Context
    private val feedsViewModel: FeedsViewModel by viewModel()

    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 4
    private var firstVisibleItemPosition = 0
    private var loading: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        feedsViewModel.feedList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                binding.rvListView.visibility = View.VISIBLE
                feedAdapter.addFeed(it)
            }
            loading = false
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerListener(view)
        initData()
    }

    private fun setRecyclerListener(view: View) {
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.rvListView.setHasFixedSize(true)
        binding.rvListView.layoutManager = layoutManager
        binding.rvListView.adapter = feedAdapter

        binding.rvListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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