package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.FeedItemClickListener

class FeedListAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalFeeds>,
    private val itemClickListener: FeedItemClickListener
) : RecyclerView.Adapter<FeedItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemHolder {
        return FeedItemHolder(parent, R.layout.feed_item_view)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: FeedItemHolder, position: Int) {
        val singleItem = mutableList[holder.adapterPosition]
        holder.bindData(singleItem)
        holder.itemView.setOnClickListener {
            itemClickListener.feedItemClickListener(singleItem, holder.adapterPosition, it)
        }
    }

    fun addFeeds(articles: MutableList<LocalFeeds>) {
        mutableList.addAll(articles)
        notifyDataSetChanged()
    }
}