package org.cpimtn.marxist.android.domain.model

/**
 * Result of a sync operation. Domain-friendly so UI/workers can show status or retry.
 */
sealed interface SyncResult {
    data object Success : SyncResult
    data class NetworkError(val message: String? = null) : SyncResult
    data class ServerError(val code: Int, val message: String? = null) : SyncResult
    data class UnknownError(val cause: Throwable? = null) : SyncResult
}
