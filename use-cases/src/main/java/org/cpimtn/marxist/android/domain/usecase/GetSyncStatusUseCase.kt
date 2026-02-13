package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.SyncStatus
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository

/**
 * Use case: observe sync status for UI ("Syncing…", "Last synced at …", "Sync failed").
 */
class GetSyncStatusUseCase(
    private val syncStatusRepository: SyncStatusRepository,
) {
    operator fun invoke(): Flow<SyncStatus> = syncStatusRepository.getSyncStatus()
}
