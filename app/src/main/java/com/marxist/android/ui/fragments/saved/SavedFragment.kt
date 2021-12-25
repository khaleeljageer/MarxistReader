package com.marxist.android.ui.fragments.saved

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.ui.activities.DetailsActivity
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.fragments.feeds.FeedsViewModel
import com.marxist.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragments_list), ItemClickListener {
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

    private val binding by viewBinding(FragmentsListBinding::bind)

    private val feedsViewModel: FeedsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvListView.setHasFixedSize(true)
        binding.rvListView.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvListView.adapter = savedAdapter

        initData()
    }

    private fun initData() {
        feedsViewModel.getDownloadedFeeds()
        feedsViewModel.feedsDownloaded.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    binding.rvListView.visibility = View.VISIBLE
                    binding.noFeed.emptyView.visibility = View.GONE
                    savedAdapter.addFeeds(it)
                } else {
                    binding.rvListView.visibility = View.GONE
                    binding.noFeed.emptyView.visibility = View.VISIBLE
                    binding.noFeed.txtTitle.text = getString(R.string.no_downloads)
                    binding.noFeed.txtHelper.text =
                        getString(R.string.you_can_add_download_from_any_article)
                    binding.noFeed.lavEmptyImage.scale = 0.25f
                    binding.noFeed.lavEmptyImage.setAnimation(R.raw.empty_music)
                }
            }
        })
    }

    private lateinit var savedAdapter: SavedListAdapter
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        savedAdapter = SavedListAdapter(mContext, mutableListOf(), this@SavedFragment)
    }
}