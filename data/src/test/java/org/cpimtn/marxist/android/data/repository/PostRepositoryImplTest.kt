package org.cpimtn.marxist.android.data.repository

import android.text.Html
import android.text.Spanned
import kotlinx.coroutines.test.runTest
import org.cpimtn.marxist.android.data.source.local.database.dao.PostDao
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSource
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.network.model.PostDTO
import org.cpimtn.marxist.network.model.RenderedText
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

/**
 * Unit tests for PostRepositoryImpl. Verifies safe sync: when fetch fails or returns null, DB is not cleared.
 *
 * fullSync branches on whether the DB is already populated, so every test states which case it is
 * exercising by stubbing [PostDao.count]. A populated DB takes the atomic accumulate-then-replace
 * path; an empty one writes each page as it arrives so the welcome screen can unblock early.
 */
class PostRepositoryImplTest {

    private val postDao: PostDao = mockk(relaxed = true)
    private val remoteDataSource: PostRemoteDataSource = mockk()
    private val repository = PostRepositoryImpl(postDao, remoteDataSource)

    /**
     * The DTO → entity mapper decodes HTML entities through [Html], which is a stubbed no-op under
     * plain JVM unit tests and throws. Stand it in with an identity decode so sync itself is what
     * these tests exercise.
     */
    @Before
    fun stubHtmlDecoding() {
        mockkStatic(Html::class)
        every { Html.fromHtml(any<String>(), any<Int>()) } answers {
            val input = firstArg<String>()
            mockk<Spanned> { every { this@mockk.toString() } returns input }
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(Html::class)
    }

    private fun postDto(id: Int) = PostDTO(
        id = id,
        date = "2024-01-01",
        slug = "slug-$id",
        title = RenderedText("Title"),
        excerpt = RenderedText("Excerpt"),
        content = RenderedText("Content"),
        tags = emptyList(),
        categories = emptyList(),
    )

    @Test
    fun fullSync_when_fetchPage_returns_null_does_not_call_replaceAll() = runTest {
        coEvery { postDao.count() } returns 100
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns null

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.ServerError)
        coVerify(exactly = 0) { postDao.replaceAll(any()) }
    }

    @Test
    fun fullSync_when_first_page_empty_does_not_call_replaceAll() = runTest {
        coEvery { postDao.count() } returns 100
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns emptyList()

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 0) { postDao.replaceAll(any()) }
    }

    @Test
    fun fullSync_when_page_returns_data_calls_replaceAll() = runTest {
        coEvery { postDao.count() } returns 100
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 50) } returns
            listOf(postDto(1))

        val result = repository.fullSync(50)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 1) { postDao.replaceAll(match { it.size == 1 }) }
    }

    @Test
    fun fullSync_when_db_populated_replaces_once_after_all_pages() = runTest {
        coEvery { postDao.count() } returns 100
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 2) } returns
            listOf(postDto(1), postDto(2))
        coEvery { remoteDataSource.fetchPage(page = 2, perPage = 2) } returns listOf(postDto(3))

        val result = repository.fullSync(2)

        assertTrue(result is SyncResult.Success)
        // Nothing is written until every page is in hand, then all three land in one transaction.
        coVerify(exactly = 0) { postDao.insertAll(any()) }
        coVerify(exactly = 1) { postDao.replaceAll(match { it.size == 3 }) }
    }

    @Test
    fun fullSync_when_db_empty_inserts_each_page_and_never_replaces() = runTest {
        coEvery { postDao.count() } returns 0
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 2) } returns
            listOf(postDto(1), postDto(2))
        coEvery { remoteDataSource.fetchPage(page = 2, perPage = 2) } returns listOf(postDto(3))

        val result = repository.fullSync(2)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 1) { postDao.insertAll(match { it.size == 2 }) }
        coVerify(exactly = 1) { postDao.insertAll(match { it.size == 1 }) }
        coVerify(exactly = 0) { postDao.replaceAll(any()) }
    }

    @Test
    fun fullSync_when_db_empty_keeps_pages_already_written_after_a_failure() = runTest {
        coEvery { postDao.count() } returns 0
        coEvery { remoteDataSource.fetchPage(page = 1, perPage = 2) } returns
            listOf(postDto(1), postDto(2))
        coEvery { remoteDataSource.fetchPage(page = 2, perPage = 2) } returns null

        val result = repository.fullSync(2)

        assertTrue(result is SyncResult.ServerError)
        // Partial data beats no data on a first install — the first page stays put.
        coVerify(exactly = 1) { postDao.insertAll(match { it.size == 2 }) }
        coVerify(exactly = 0) { postDao.clearAll() }
    }
}
