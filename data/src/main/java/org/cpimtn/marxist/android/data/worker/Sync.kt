package org.cpimtn.marxist.android.data.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager

object Sync {
    /**
     * Initializes the sync process that keeps the app's data current.
     * This method should be called only once from the app module's Application.onCreate().
     *
     * @param context The application context.
     */
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                "sync_posts",
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
            enqueueUniqueWork(
                "sync_taxonomy",
                ExistingWorkPolicy.KEEP,
                TaxonomySyncWorker.taxonomySyncWork(),
            )
        }
    }
}