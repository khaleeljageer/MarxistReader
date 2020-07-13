package com.marxist.android.utils.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.marxist.android.database.AppDatabase

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        if (id in DownloadManagerHelper.getAudioDownloads(context)) {
            val localBooksDao =
                AppDatabase.getAppDatabase(context.applicationContext).localFeedsDao()
            val file = DownloadManagerHelper.getDownloadedFile(context, id)
            if (file.isSuccessful()) {
                localBooksDao.updateAudioStatus(true, file.title!!)
            }
        } else if (id in DownloadManagerHelper.getBooksDownloads(context)) {
            val localBooksDao =
                AppDatabase.getAppDatabase(context.applicationContext).localBooksDao()
            val file = DownloadManagerHelper.getDownloadedFile(context, id)
            if (file.isSuccessful()) {
//                localBooksDao.updateBookStatus(true, id)
            }
        }
    }
}