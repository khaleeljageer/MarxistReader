package com.marxist.android.ui.fragments.feeds

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.data.model.WPPost
import com.marxist.android.databinding.FeedItemViewBinding
import com.marxist.android.databinding.ListFeedsBottomProgressBinding
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.ui.base.ProgressViewHolder

class FeedListAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<WPPost?>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            val binding = FeedItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FeedItemHolder(binding)
        } else {
            val binding = ListFeedsBottomProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ProgressViewHolder(binding)
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

    fun addFeed(articles: List<WPPost>) {
        mutableList.addAll(articles)
        notifyItemRangeInserted(mutableList.size, articles.size)
    }

    fun addFeeds(articles: MutableList<WPPost>) {
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

    fun clear() {
        mutableList.clear()
        notifyDataSetChanged()
    }
}