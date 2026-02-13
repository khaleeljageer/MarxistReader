package org.cpimtn.marxist.android.domain.usecase

import kotlinx.coroutines.test.runTest
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertTrue

/**
 * Unit tests for SyncPostsUseCase. No Android; mocks repositories.
 */
class SyncPostsUseCaseTest {

    private val postRepository: PostRepository = mockk(relaxed = true)
    private val syncStatusRepository: SyncStatusRepository = mockk(relaxed = true)
    private val useCase = SyncPostsUseCase(postRepository, syncStatusRepository, defaultPerPage = 30)

    @Test
    fun invoke_on_success_records_sync_success() = runTest {
        coEvery { postRepository.fullSync(any()) } returns SyncResult.Success

        val result = useCase(50)

        assertTrue(result is SyncResult.Success)
        coVerify { syncStatusRepository.setSyncing(true) }
        coVerify { syncStatusRepository.recordSyncSuccess() }
        coVerify { syncStatusRepository.setSyncing(false) }
        coVerify(exactly = 1) { postRepository.fullSync(50) }
    }

    @Test
    fun invoke_on_network_error_records_sync_error() = runTest {
        coEvery { postRepository.fullSync(any()) } returns SyncResult.NetworkError("offline")

        val result = useCase(50)

        assertTrue(result is SyncResult.NetworkError)
        coVerify { syncStatusRepository.recordSyncError("offline") }
        coVerify { syncStatusRepository.setSyncing(false) }
    }
}
