package com.marxist.android.ui.fragments.books

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BookClickListener
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions

class BookListAdapter(
    private val mContext: Context,
    private var booksList: MutableList<LocalBooks>,
    private val listener: BookClickListener
) : RecyclerView.Adapter<BookViewHolder>() {
    private lateinit var rvListView: RecyclerView
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
        holder.bindData(bookItem, position)
    }

    private fun downloadClicked() {
        Permissions.check(
            mContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            null,
            object : PermissionHandler() {
                override fun onGranted() {

                }
            })
    }

    fun setItems(it: MutableList<LocalBooks>) {
        booksList = it
        notifyDataSetChanged()
    }

    fun updateDownloadId(itemPosition: Int) {
        previousClickedPosition = -1
        notifyItemChanged(itemPosition)
    }

    fun setListView(rvListView: RecyclerView) {
        this.rvListView = rvListView
    }

}