package org.cpimtn.marxist.android.data.worker

import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.cpimtn.marxist.android.domain.usecase.SyncTaxonomyUseCase

@HiltWorker
class TaxonomySyncWorker @AssistedInject constructor(
    @Assisted private val appContext: android.content.Context,
    @Assisted workerParams: WorkerParameters,
    private val syncTaxonomyUseCase: SyncTaxonomyUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result =
        syncTaxonomyUseCase().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )

    companion object {
        fun taxonomySyncWork() = OneTimeWorkRequestBuilder<TaxonomySyncWorker>().build()
    }
}
