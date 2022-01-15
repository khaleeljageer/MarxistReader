package com.marxist.android.ui.fragments.quotes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.marxist.android.R
import com.marxist.android.databinding.FragmentsListBinding
import com.marxist.android.model.Quote
import com.marxist.android.ui.base.QuoteClickListener
import com.marxist.android.utils.AppConstants
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

    override fun quoteClickListener(quote: Quote, type: Int) {
        when (type) {
            AppConstants.FACEBOOK_SHARE -> {
                if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                    val content = ShareLinkContent.Builder()
                        .setContentUrl(
                            Uri.parse(
                                if (quote.link.isEmpty()) {
                                    "https://marxistreader.home.blog/"
                                } else quote.link
                            )
                        )
                        .setQuote(quote.quote)
                        .setShareHashtag(
                            ShareHashtag.Builder()
                                .setHashtag(quote.hashTag)
                                .build()
                        )
                        .build()

                    ShareDialog.show(requireActivity(), content)
                }
            }

            AppConstants.OTHER_SHARE -> {
                val sendIntent: Intent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(
                        Intent.EXTRA_TEXT,
                        "${quote.quote}\n${quote.reference}\n${quote.link}"
                    )
                    this.type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "Choose app to share")
                startActivity(shareIntent)
            }
        }
    }

    companion object {
        fun newInstance(): QuotesFragment {
            return QuotesFragment()
        }
    }
}