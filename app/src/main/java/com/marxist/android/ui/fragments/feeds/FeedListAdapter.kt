package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.base.ProgressViewHolder

class FeedListAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalFeeds?>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            FeedItemHolder(parent, R.layout.feed_item_view)
        } else {
            ProgressViewHolder(parent, R.layout.list_feeds_bottom_progress)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mutableList[position] == null) {
            VIEW_PROG
        } else {
            VIEW_ITEM
        }
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FeedItemHolder) {
            val singleItem = mutableList[holder.adapterPosition]
            singleItem?.let { feeds ->
                holder.bindData(feeds)
                holder.itemView.setOnClickListener {
                    itemClickListener.feedItemClickListener(singleItem, holder.adapterPosition, it)
                }
            }
        } else {
            (holder as ProgressViewHolder).bindData("")
        }
    }

    fun addFeed(article: LocalFeeds) {
        mutableList.add(article)
        notifyItemInserted(mutableList.size - 1)
    }

    fun addFeeds(articles: MutableList<LocalFeeds>) {
        mutableList.addAll(articles)
        notifyDataSetChanged()
    }

    fun addLoaderItem() {
        mutableList.add(null)
        notifyItemInserted(mutableList.size + 1)
    }

    fun removeLoaderItem() {
        this.mutableList.removeAt(itemCount - 1)
        notifyItemRemoved(itemCount)
    }
}