package com.marxist.android.ui.fragments.books

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.databinding.BookListItemBinding


class BookViewHolder(
    private val binding: BookListItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(
        book: LocalBooks,
        listener: (LocalBooks) -> Unit
    ) {
        binding.arBookImage.load(book.image) {
            placeholder(R.drawable.placeholder)
        }
        binding.pbDownloadProgress.visibility = if (book.isDownloading) {
            View.VISIBLE
        } else {
            View.GONE
        }
        itemView.setOnClickListener {
            listener.invoke(book)
        }
    }
}