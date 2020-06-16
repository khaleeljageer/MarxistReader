package com.marxist.android.ui.fragments.books

import android.view.View
import android.view.ViewGroup
import coil.api.load
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BaseBookViewHolder
import kotlinx.android.synthetic.main.book_list_item.view.*


class BookViewHolder(
    private val viewGroup: ViewGroup, view: View
) :
    BaseBookViewHolder<LocalBooks>(view) {
    override fun bindData(book: LocalBooks) {
        itemView.arBookImage.load(book.image)
        val expanded = book.isExpanded
        itemView.fabDownload.run {
            show()
            itemView.pbDownloadProgress.visibility = View.INVISIBLE
            if (book.isDownloaded) {
                setImageResource(R.drawable.ic_chrome_reader_mode_black_24dp)
            } else {
                setImageResource(R.drawable.ic_save_black)
            }
        }
        // Set the visibility based on state
        itemView.rlDownloadView.visibility = if (expanded) View.VISIBLE else View.GONE
    }
}