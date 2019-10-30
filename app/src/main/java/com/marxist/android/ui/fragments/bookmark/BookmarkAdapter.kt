package com.marxist.android.ui.fragments.bookmark

import android.content.Context
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalHighlights
import com.marxist.android.ui.base.ItemClickListener
import com.marxist.android.utils.DeviceUtils
import kotlinx.android.synthetic.main.highlight_item_view.view.*

class BookmarkAdapter(
    private val mContext: Context,
    private val mutableList: MutableList<LocalHighlights>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<BookmarkItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkItemHolder {
        return BookmarkItemHolder(parent, R.layout.highlight_item_view)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: BookmarkItemHolder, position: Int) {
        val item = mutableList[holder.adapterPosition]
        holder.bindData(item)
        holder.itemView.setOnClickListener {
            showPopupMenu(item, holder)
        }
        holder.itemView.txtMore.setOnClickListener {
            showPopupMenu(item, holder)
        }
    }

    private fun showPopupMenu(
        item: LocalHighlights,
        holder: BookmarkItemHolder
    ) {
        val popup = PopupMenu(mContext, holder.itemView.txtMore)
        popup.inflate(R.menu.menu_bookmar_popup)
        popup.setOnMenuItemClickListener {
            if (it != null) {
                when (it.itemId) {
                    R.id.menu_share -> {
                        DeviceUtils.shareIntent(
                            item.title, item.highlight.plus("\n")
                                .plus(mContext.getString(R.string.to_read_more))
                                .plus(item.link), mContext
                        )
                    }
                    R.id.menu_remove -> {
                        mutableList.removeAt(holder.adapterPosition)
                        itemClickListener.feedItemClickListener(
                            item,
                            holder.adapterPosition,
                            holder.itemView.txtMore
                        )
                    }
                }
            }
            false
        }
        popup.show()
    }

    fun addHighlights(highLights: MutableList<LocalHighlights>) {
        mutableList.addAll(highLights)
        notifyDataSetChanged()
    }
}