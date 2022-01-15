package com.marxist.android.ui.fragments.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.databinding.BookListItemBinding
import com.marxist.android.utils.AppConstants

class BookListAdapter(
    private var booksList: MutableList<LocalBooks>,
    private val listener: (LocalBooks) -> Unit
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            BookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val lp = binding.root.layoutParams
        val width = parent.measuredWidth
        lp.height = ((width / AppConstants.ASPECT_RATIO) / 4).toInt()
        binding.root.layoutParams = lp

        return BookViewHolder(binding)
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
        holder.bindData(getItem(position), listener)
    }

    fun setItems(it: MutableList<LocalBooks>) {
        booksList = it
        notifyDataSetChanged()
    }

    fun setDownloading(book: LocalBooks, isDownloading: Boolean) {
        getBook(book)?.isDownloading = isDownloading
        notifyItemChanged(booksList.indexOf(book))
    }

    private fun getBook(book: LocalBooks) = booksList.find { book.bookid == it.bookid }
}