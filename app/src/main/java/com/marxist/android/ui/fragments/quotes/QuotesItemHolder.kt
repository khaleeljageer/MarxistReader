package com.marxist.android.ui.fragments.quotes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.HighlightItemViewBinding
import com.marxist.android.model.Quote
import com.marxist.android.ui.base.QuoteClickListener
import com.marxist.android.utils.AppConstants

class QuotesItemHolder(
    private val parent: HighlightItemViewBinding,
    private val itemClickListener: QuoteClickListener
) :
    RecyclerView.ViewHolder(parent.root) {
    fun bindData(quote: Quote) {
        parent.txtQuote.text = quote.quote
        if (quote.reference.isNotEmpty()) {
            parent.txtQuoteReference.visibility = View.VISIBLE
            parent.txtQuoteReference.text = quote.reference
        } else {
            parent.txtQuoteReference.visibility = View.GONE
        }
        parent.ivShare.setOnClickListener {
            itemClickListener.quoteClickListener(quote, AppConstants.OTHER_SHARE)
        }

        parent.ivFacebookShare.setOnClickListener {
            itemClickListener.quoteClickListener(quote, AppConstants.FACEBOOK_SHARE)
        }
    }
}