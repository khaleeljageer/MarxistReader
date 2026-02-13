package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.SyncStatus

/**
 * Persists and exposes sync status (last success time, last error) for offline-first UI.
 */
interface SyncStatusRepository {
    fun getSyncStatus(): Flow<SyncStatus>
    suspend fun setSyncing(syncing: Boolean)
    suspend fun recordSyncSuccess()
    suspend fun recordSyncError(message: String)
}
