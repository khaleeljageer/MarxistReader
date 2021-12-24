package com.marxist.android.ui.fragments.saved

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.databinding.FeedItemViewBinding
import com.marxist.android.ui.base.ItemClickListener

class SavedListAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalFeeds>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<SavedItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedItemHolder {
        val binding = FeedItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedItemHolder(binding)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: SavedItemHolder, position: Int) {
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