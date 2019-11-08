package com.marxist.android.ui.fragments.books

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BookClickListener
import kotlinx.android.synthetic.main.book_list_item.view.*

class BookListAdapter(
    private val mContext: Context,
    private var booksList: MutableList<LocalBooks>,
    private val listener: BookClickListener
) : RecyclerView.Adapter<BookViewHolder>() {
    private var previousClickedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutId = R.layout.book_list_item
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        lp.height = (parent.measuredHeight / 2.5).toInt()
        view.layoutParams = lp
        return BookViewHolder(parent, view)
    }

    override fun getItemCount(): Int = booksList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val bookItem = booksList[holder.adapterPosition]
        holder.bindData(bookItem)

        holder.itemView.fabDownload.setOnClickListener {
            if (!bookItem.isDownloaded) {
                holder.itemView.fabDownload.hide()
                holder.itemView.pbDownloadProgress.visibility = View.VISIBLE
            }
            listener.bookItemClickListener(holder.adapterPosition, bookItem)
        }

        holder.itemView.setOnClickListener {
            if (previousClickedPosition == holder.adapterPosition) {
                return@setOnClickListener
            }
            if (previousClickedPosition != -1) {
                booksList[previousClickedPosition].isExpanded = false
                notifyItemChanged(previousClickedPosition)
            }
            previousClickedPosition = holder.adapterPosition
            val expanded = bookItem.isExpanded
            bookItem.isExpanded = !expanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

    fun setItems(it: MutableList<LocalBooks>) {
        booksList = it
        notifyDataSetChanged()
    }

    fun updateDownloadId(itemPosition: Int, downloadID: Long) {
        booksList[itemPosition].downloadId = downloadID
        notifyItemChanged(itemPosition)
    }
}