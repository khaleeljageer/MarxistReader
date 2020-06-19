package com.marxist.android.ui.fragments.books

import android.app.DownloadManager
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.api.load
import com.marxist.android.database.entities.LocalBooks
import com.marxist.android.ui.base.BaseBookViewHolder
import com.marxist.android.utils.download.DownloadUtil
import ir.siaray.downloadmanagerplus.classes.Downloader
import ir.siaray.downloadmanagerplus.enums.DownloadReason
import ir.siaray.downloadmanagerplus.enums.DownloadStatus
import ir.siaray.downloadmanagerplus.interfaces.DownloadListener
import kotlinx.android.synthetic.main.book_list_item.view.*
import java.io.File


class BookViewHolder(
    private val viewGroup: ViewGroup,
    view: View
) : BaseBookViewHolder<LocalBooks>(view) {
    lateinit var downloadListener: DownloadListener
    override fun bindData(
        book: LocalBooks,
        position: Int
    ) {
        itemView.arBookImage.load(book.image) {
            size(160, 360)
        }
        initListener(book)
        itemView.setOnClickListener {
            val downloader = getDownloader(book.epub, book.title, book.bookid)
            if (downloader.getStatus(book.bookid) == DownloadStatus.RUNNING || downloader.getStatus(
                    book.bookid
                ) == DownloadStatus.PENDING
            ) {
                Toast.makeText(viewGroup.context, "Downloading", Toast.LENGTH_SHORT).show()
            } else if (downloader.getStatus(book.bookid) == DownloadStatus.SUCCESSFUL) {

                DownloadUtil.openSavedBook(
                    viewGroup.context, if (book.savedPath.isEmpty()) {
                        downloader.getDownloadedFilePath(book.bookid)
                    } else {
                        book.savedPath
                    }
                )
            } else {
                downloader.start()
            }
        }
    }

    private fun initListener(
        book: LocalBooks
    ) {
        downloadListener = object : DownloadListener {
            override fun onPending(percent: Int, totalBytes: Int, downloadedBytes: Int) {

            }

            override fun onComplete(totalBytes: Int) {
                val filePath =
                    File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "/Marxist Reader/"
                    )
                book.savedPath = "${filePath.absolutePath}/${book.title}.epub"
                book.downloadPercent = 100
                book.isDownloaded = true
            }

            override fun onFail(
                percent: Int,
                reason: DownloadReason?,
                totalBytes: Int,
                downloadedBytes: Int
            ) {
                book.isDownloaded = false
            }

            override fun onCancel(totalBytes: Int, downloadedBytes: Int) {

            }

            override fun onPause(
                percent: Int,
                reason: DownloadReason?,
                totalBytes: Int,
                downloadedBytes: Int
            ) {

            }

            override fun onRunning(
                percent: Int,
                totalBytes: Int,
                downloadedBytes: Int,
                downloadSpeed: Float
            ) {
                itemView.pbDownloadProgress.progress = percent
            }
        }
    }

    private fun getDownloader(link: String, title: String, token: String): Downloader {
        return Downloader.getInstance(viewGroup.context)
            .setToken(token)
            .setListener(downloadListener)
            .setUrl(link).setAllowedOverRoaming(true).setAllowedOverMetered(true)
            .setVisibleInDownloadsUi(true)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationDir(Environment.DIRECTORY_DOCUMENTS, "/Marxist Reader/$title.epub")
            .setNotificationTitle(title).setKeptAllDownload(false)
    }
}