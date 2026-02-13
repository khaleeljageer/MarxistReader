package org.cpimtn.marxist.android.domain.usecase

import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository

/**
 * Use case: trigger a full sync of posts from the server into local storage.
 * Records sync outcome in SyncStatusRepository for UI (Phase 6).
 */
class SyncPostsUseCase(
    private val postRepository: PostRepository,
    private val syncStatusRepository: SyncStatusRepository,
    private val defaultPerPage: Int,
) {
    suspend operator fun invoke(perPage: Int = defaultPerPage): SyncResult {
        syncStatusRepository.setSyncing(true)
        return try {
            when (val result = postRepository.fullSync(perPage)) {
                is SyncResult.Success -> {
                    syncStatusRepository.recordSyncSuccess()
                    result
                }
                is SyncResult.NetworkError -> {
                    syncStatusRepository.recordSyncError(result.message ?: "Network error")
                    result
                }
                is SyncResult.ServerError -> {
                    syncStatusRepository.recordSyncError(result.message ?: "Server error")
                    result
                }
                is SyncResult.UnknownError -> {
                    syncStatusRepository.recordSyncError(result.cause?.message ?: "Unknown error")
                    result
                }
            }
        } finally {
            syncStatusRepository.setSyncing(false)
        }
    }
}
