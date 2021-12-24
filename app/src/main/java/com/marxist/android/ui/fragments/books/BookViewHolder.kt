package com.marxist.android.ui.fragments.books

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.download.library.DownloadImpl
import com.download.library.DownloadListenerAdapter
import com.download.library.Extra
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.databinding.BookListItemBinding
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.download.DownloadUtil
import java.io.File


class BookViewHolder(
    private val context: Context,
    private val binding: BookListItemBinding,
    private val targetPath: String
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(
        book: LocalBooks,
        appDatabase: AppDatabase
    ) {
        binding.arBookImage.load(book.image) {
            placeholder(R.drawable.placeholder)
        }
        itemView.setOnClickListener {
            val isExist = appDatabase.localBooksDao().getDownloadStatus(book.bookid)
            if (isExist) {
                val path = File(targetPath, "${book.title}.epub")
                val fileExist = path.exists()
                if (fileExist) {
                    DownloadUtil.openSavedBook(context, path.toString())
                } else {
                    binding.pbDownloadProgress.visibility = View.VISIBLE
                    downloadFile(book, appDatabase)
                }
            } else {
                binding.pbDownloadProgress.visibility = View.VISIBLE
                downloadFile(book, appDatabase)
            }
        }
    }

    private fun downloadFile(
        book: LocalBooks,
        appDatabase: AppDatabase
    ) {
        DownloadImpl.getInstance(context)
            .with(book.epub)
            .setUniquePath(true)
            .setEnableIndicator(false)
            .setRetry(3)
            .setParallelDownload(true)
            .setForceDownload(true)
            .target(File(targetPath, "${book.title}.epub"))
            .url(book.epub).enqueue(object : DownloadListenerAdapter() {
                override fun onStart(
                    url: String?,
                    userAgent: String?,
                    contentDisposition: String?,
                    mimetype: String?,
                    contentLength: Long,
                    extra: Extra?
                ) {
                    binding.pbDownloadProgress.visibility = View.VISIBLE
                    RxBus.publish(ShowSnackBar("${book.title} Downloading"))
                }

                override fun onProgress(
                    url: String?,
                    downloaded: Long,
                    length: Long,
                    usedTime: Long
                ) {
                    val percent = (downloaded.div(length)) * 100
                    binding.pbDownloadProgress.progress = percent.toInt()
                }

                override fun onResult(
                    throwable: Throwable?,
                    path: Uri?,
                    url: String?,
                    extra: Extra?
                ): Boolean {
                    url?.let {
                        val isExist1 = DownloadImpl.getInstance(context).exist(book.epub)
                        if (isExist1) {
                            binding.pbDownloadProgress.visibility = View.INVISIBLE
                            RxBus.publish(ShowSnackBar("${book.title} Completed"))
                            appDatabase.localBooksDao()
                                .updateDownloadDetails(path.toString(), true, book.bookid)
                        }
                    }
                    return super.onResult(throwable, path, url, extra)
                }
            })
    }
}