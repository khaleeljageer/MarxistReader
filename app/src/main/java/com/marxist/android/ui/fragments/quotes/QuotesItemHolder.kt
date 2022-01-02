package com.marxist.android.ui.fragments.quotes

import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.HighlightItemViewBinding
import com.marxist.android.model.Quote
import com.marxist.android.ui.base.QuoteClickListener

class QuotesItemHolder(
    private val parent: HighlightItemViewBinding,
    private val itemClickListener: QuoteClickListener
) :
    RecyclerView.ViewHolder(parent.root) {
    fun bindData(quote: Quote) {
        parent.txtQuote.text = quote.quote
        parent.root.setOnClickListener {
            itemClickListener.quoteClickListener(quote)
        }
    }
}