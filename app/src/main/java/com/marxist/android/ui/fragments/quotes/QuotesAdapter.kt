package com.marxist.android.ui.fragments.quotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.databinding.HighlightItemViewBinding
import com.marxist.android.model.Quote
import com.marxist.android.ui.base.QuoteClickListener

class QuotesAdapter(
    private val mutableList: MutableList<Quote>,
    private val itemClickListener: QuoteClickListener
) : RecyclerView.Adapter<QuotesItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotesItemHolder {
        val binding =
            HighlightItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuotesItemHolder(binding, itemClickListener)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: QuotesItemHolder, position: Int) {
        val item = mutableList[holder.adapterPosition]
        holder.bindData(item)
    }

    fun updateQuotes(highLights: List<Quote>) {
        mutableList.clear()
        mutableList.addAll(highLights)
        notifyDataSetChanged()
    }
}