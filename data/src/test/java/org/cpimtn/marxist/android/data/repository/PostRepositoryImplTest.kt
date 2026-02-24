package org.cpimtn.marxist.android.data.repository

import kotlinx.coroutines.test.runTest
import org.cpimtn.marxist.android.data.source.local.database.dao.PostDao
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSource
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.network.model.PostDTO
import org.cpimtn.marxist.network.model.RenderedText
import org.junit.Assert.assertTrue
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

/**
 * Unit tests for PostRepositoryImpl. Verifies safe sync: when fetch fails or returns null, DB is not cleared.
 */
class PostRepositoryImplTest {

    private val postDao: PostDao = mockk(relaxed = true)
    private val remoteDataSource: PostRemoteDataSource = mockk()
    private val repository = PostRepositoryImpl(postDao, remoteDataSource)

    @Test
    fun fullSync_when_fetchPage_returns_null_does_not_call_replaceAll() = runTest {
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns null

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.ServerError)
        coVerify(exactly = 0) { postDao.replaceAll(any()) }
    }

    @Test
    fun fullSync_when_first_page_empty_does_not_call_replaceAll() = runTest {
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns emptyList()

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 0) { postDao.replaceAll(any()) }
    }

    @Test
    fun fullSync_when_page_returns_data_calls_replaceAll() = runTest {
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns listOf(
            PostDTO(1, "2024-01-01", "slug", RenderedText("Title"), RenderedText("Excerpt"), emptyList(), emptyList())
        )

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 1) { postDao.replaceAll(match { it.size == 1 }) }
    }
}
