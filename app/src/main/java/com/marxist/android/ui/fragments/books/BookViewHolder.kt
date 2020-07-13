package com.marxist.android.ui.fragments.books

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.download.library.DownloadImpl
import com.download.library.DownloadListenerAdapter
import com.download.library.Extra
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.utils.download.DownloadUtil
import kotlinx.android.synthetic.main.book_list_item.view.*
import java.io.File


class BookViewHolder(
    private val context: Context,
    view: View,
    private val targetPath: String
) : RecyclerView.ViewHolder(view) {

    fun bindData(
        book: LocalBooks,
        position: Int,
        appDatabase: AppDatabase
    ) {
        itemView.arBookImage.load(book.image)
        itemView.setOnClickListener {
            val isExist = appDatabase.localBooksDao().getDownloadStatus(book.bookid)
            if (isExist) {
                DownloadUtil.openSavedBook(
                    context, if (book.savedPath.isEmpty()) {
                        appDatabase.localBooksDao().getSavedPath(book.bookid)
                    } else {
                        book.savedPath
                    }
                )
            } else {
                itemView.pbDownloadProgress.visibility = View.VISIBLE
                DownloadImpl.getInstance()
                    .with(context)
                    .target(File(targetPath, "${book.title}.epub"))
                    .setUniquePath(true)
                    .setEnableIndicator(true)
                    .setRetry(3)
                    .setParallelDownload(true)
                    .setForceDownload(true)
                    .url(book.epub).enqueue(object : DownloadListenerAdapter() {
                        override fun onStart(
                            url: String?,
                            userAgent: String?,
                            contentDisposition: String?,
                            mimetype: String?,
                            contentLength: Long,
                            extra: Extra?
                        ) {
                            itemView.pbDownloadProgress.visibility = View.VISIBLE
                            Toast.makeText(context, "Download Started...", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onProgress(
                            url: String?,
                            downloaded: Long,
                            length: Long,
                            usedTime: Long
                        ) {
                            val percent = (downloaded.div(length)) * 100
                            itemView.pbDownloadProgress.progress = percent.toInt()
                        }

                        override fun onResult(
                            throwable: Throwable?,
                            path: Uri?,
                            url: String?,
                            extra: Extra?
                        ): Boolean {
                            url?.let {
                                val isExist1 = DownloadImpl.getInstance().exist(book.epub)
                                if (isExist1) {
                                    itemView.pbDownloadProgress.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        context,
                                        "Download Completed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    appDatabase.localBooksDao()
                                        .updateDownloadDetails(path.toString(), true, book.bookid)
                                }
                            }
                            return super.onResult(throwable, path, url, extra)
                        }
                    })
            }
        }
    }
}