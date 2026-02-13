package org.cpimtn.marxist.android.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.cpimtn.marxist.android.domain.repository.PostRepository
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = try {
        val perPage = inputData.getInt(KEY_PER_PAGE, 50)
        postRepository.fullSync(perPage)
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }

    companion object {
        private const val KEY_PER_PAGE = "per_page"
        const val TOTAL_SYNC_ATTEMPTS = 3

        /**
         * Network constraints for syncing data.
         */
        val SyncConstraints
            get() = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        /**
         * Starts the work to sync data.
         * @return The one-time work request to sync data.
         */
        fun startUpSyncWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(workDataOf(KEY_PER_PAGE to 50))
                .setConstraints(SyncConstraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    backoffDelay = 3,
                    TimeUnit.SECONDS
                )
                .build()
        }
    }
}
