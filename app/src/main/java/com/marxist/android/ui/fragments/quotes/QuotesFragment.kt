package com.marxist.android.ui.fragments.quotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.marxist.android.R
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.model.Quote
import com.marxist.android.ui.base.QuoteClickListener
import com.marxist.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuotesFragment : Fragment(R.layout.fragments_list), QuoteClickListener {
    private val binding by viewBinding(FragmentsListBinding::bind)
    private val viewModel: QuotesViewModel by viewModels()

    private val quotesAdapter: QuotesAdapter by lazy {
        QuotesAdapter(mutableListOf(), this)
    }

    private fun initData() {
        viewModel.getNotification()
        viewModel.notifications.observe(viewLifecycleOwner, {
            binding.rvListView.visibility = View.VISIBLE
            binding.noFeed.emptyView.visibility = View.GONE
            binding.progressLoader.visibility = View.GONE
            quotesAdapter.updateQuotes(it)
        })
        viewModel.loading.observe(viewLifecycleOwner, {

            binding.rvListView.visibility = View.GONE
            binding.noFeed.emptyView.visibility = View.GONE
            binding.progressLoader.visibility = View.VISIBLE
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.rvListView) {
            val stagManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            setHasFixedSize(true)
            layoutManager = stagManager
            adapter = quotesAdapter
        }
        initData()
    }

    override fun quoteClickListener(quote: Quote) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${quote.quote}\n${quote.link}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Choose app to share")
        startActivity(shareIntent)
    }
}