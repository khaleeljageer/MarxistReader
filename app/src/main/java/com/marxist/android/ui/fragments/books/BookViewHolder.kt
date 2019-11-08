package com.marxist.android.ui.fragments.books

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.marxist.android.R
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BaseBookViewHolder
import kotlinx.android.synthetic.main.book_list_item.view.*


class BookViewHolder(
    private val viewGroup: ViewGroup, view: View
) :
    BaseBookViewHolder<LocalBooks>(view) {
    override fun bindData(book: LocalBooks) {
        Glide.with(viewGroup.context)
            .load(book.image)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .override(212, 300)
            .into(itemView.arBookImage)
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


/*
    override fun bindData(book: LocalBooks, adapterPosition: Int) {
        *//*Glide.with(mContext)
            .load(book.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(212, 300)
            .into(itemView.arBookImage)*//*

        val expanded = book.isExpanded

        itemView.fabDownload.run {
            if (book.isDownloaded) {
                itemView.fabDelete.visibility = View.VISIBLE
                itemView.fabDownload.visibility = View.VISIBLE
                itemView.pbDownloadProgress.visibility = View.INVISIBLE
                setImageResource(R.drawable.ic_chrome_reader_mode_black_24dp)
            } else {
                itemView.fabDelete.visibility = View.INVISIBLE
                itemView.fabDownload.visibility = View.VISIBLE
                itemView.pbDownloadProgress.visibility = View.INVISIBLE
                setImageResource(R.drawable.ic_save_black)
            }
        }

        // Set the visibility based on state
        itemView.rlDownloadView.visibility = if (expanded) View.VISIBLE else View.GONE
        if (expanded) {
            val scaleAnim = ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            val alphaAnim = AlphaAnimation(0f, 1f)
            val animSet = AnimationSet(true)
            animSet.addAnimation(scaleAnim)
            animSet.addAnimation(alphaAnim)
            animSet.isFillEnabled = true

            animSet.interpolator = OvershootInterpolator()
            animSet.duration = 300
            animSet.startOffset = 100
            itemView.fabDownload.startAnimation(animSet)
        }
        itemView.fabDelete.setOnClickListener {
            listener.bookRemoveClickListener(adapterPosition, book)
        }

        itemView.fabDownload.setOnClickListener {
            if (!book.isDownloaded) {
                itemView.fabDownload.visibility = View.INVISIBLE
                itemView.pbDownloadProgress.visibility = View.VISIBLE
            }
            listener.bookItemClickListener(adapterPosition, book)
        }
    }*/
}