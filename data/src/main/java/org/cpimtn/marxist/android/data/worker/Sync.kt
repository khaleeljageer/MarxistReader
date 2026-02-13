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
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                "sync_posts",
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
        }
    }
}