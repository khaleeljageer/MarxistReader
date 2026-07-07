package org.cpimtn.marxist.android.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.usecase.SyncBooksUseCase

@HiltWorker
class BookSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncBooksUseCase: SyncBooksUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = when (syncBooksUseCase()) {
        is SyncResult.Success -> Result.success()
        else -> Result.retry()
    }

    companion object {
        fun syncWork() = OneTimeWorkRequestBuilder<BookSyncWorker>().build()
    }
}
