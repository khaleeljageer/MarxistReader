package org.cpimtn.marxist.android.domain.model

/**
 * Observable sync state for UI: show "Syncing…", "Last synced at …", or "Sync failed".
 */
data class SyncStatus(
    val isSyncing: Boolean = false,
    val lastSyncedAtMillis: Long? = null,
    val lastError: String? = null,
) {
    val hasError: Boolean get() = lastError != null
}
