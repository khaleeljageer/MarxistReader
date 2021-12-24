package com.marxist.android.ui.fragments.books

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.databinding.BookListItemBinding
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.DeviceUtils

class BookListAdapter(
    private val mContext: Context,
    private var booksList: MutableList<LocalBooks>,
    private val appDatabase: AppDatabase
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            BookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val lp = binding.root.layoutParams
        val width = parent.measuredWidth
        lp.height = ((width / AppConstants.ASPECT_RATIO) / 2.5).toInt()
        binding.root.layoutParams = lp

        val targetPath = DeviceUtils.getRootDirPath(mContext).plus("/books")
        return BookViewHolder(mContext, binding, targetPath)
    }

    override fun getItemCount(): Int {
        return if (booksList.isNullOrEmpty()) 0 else booksList.size
    }

    private fun getItem(position: Int): LocalBooks {
        return booksList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bindData(getItem(position), appDatabase)
    }

    fun setItems(it: MutableList<LocalBooks>) {
        booksList = it
        notifyDataSetChanged()
    }
}