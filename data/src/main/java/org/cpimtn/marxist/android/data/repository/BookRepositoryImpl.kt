package org.cpimtn.marxist.android.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.cpimtn.marxist.android.data.source.local.database.dao.BookDao
import org.cpimtn.marxist.android.data.source.local.database.dao.BookReaderLinkDao
import org.cpimtn.marxist.android.data.source.local.database.entity.BookEntity
import org.cpimtn.marxist.android.data.source.local.database.entity.BookReaderLinkEntity
import org.cpimtn.marxist.android.data.source.local.database.mapper.toDomain
import org.cpimtn.marxist.android.data.source.local.database.mapper.toEntity
import org.cpimtn.marxist.android.data.source.remote.BookRemoteDataSource
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.domain.model.BookDownloadState
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.repository.BookRepository
import org.cpimtn.marxist.core.getDownloadDir
import org.cpimtn.marxist.network.downloader.FileDownloader
import org.cpimtn.marxist.network.utils.DownloadResult
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Offline First: getBooks() reads from local DB; fullSync() fetches the catalog from remote,
 * then replaces DB in one transaction so a failed sync never wipes data.
 * Downloading an epub is a separate, on-demand action tracked by local file presence
 * rather than a DB column, since the file itself is the source of truth for "downloaded".
 */
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val bookReaderLinkDao: BookReaderLinkDao,
    private val remoteDataSource: BookRemoteDataSource,
    private val fileDownloader: FileDownloader,
    @ApplicationContext private val context: Context,
) : BookRepository {

    override fun getBooks(): Flow<List<Book>> =
        bookDao.getAll().map { entities -> entities.map(BookEntity::toDomain) }

    override suspend fun fullSync(): SyncResult = try {
        val books = remoteDataSource.fetchBooks()
        when {
            books == null -> SyncResult.ServerError(
                code = -1,
                message = "Request failed or empty response"
            )

            else -> {
                val entities = books.mapIndexed { index, dto -> dto.toEntity(index) }
                if (entities.isNotEmpty()) bookDao.replaceAll(entities)
                SyncResult.Success
            }
        }
    } catch (e: IOException) {
        SyncResult.NetworkError(e.message)
    } catch (e: Exception) {
        SyncResult.UnknownError(e)
    }

    override fun downloadBook(book: Book): Flow<BookDownloadState> = flow {
        emit(BookDownloadState.Downloading)
        val destination = File(context.getDownloadDir(), fileNameFor(book.id))
        coroutineScope {
            fileDownloader.downloadFile(
                url = book.epubUrl,
                uniqueId = book.id,
                fileName = book.title,
                destinationPath = destination.absolutePath,
                coroutineScope = this,
            ).collect { result ->
                when (result) {
                    is DownloadResult.Success ->
                        emit(BookDownloadState.Downloaded(result.file.absolutePath))

                    is DownloadResult.Error ->
                        emit(BookDownloadState.Failed(result.message))

                    is DownloadResult.Queued, is DownloadResult.Progress -> Unit
                }
            }
        }
    }

    override suspend fun getDownloadedBookIds(): Set<String> = withContext(Dispatchers.IO) {
        context.getDownloadDir()
            .listFiles { file -> file.isFile && file.extension == EPUB_EXTENSION }
            ?.map { it.nameWithoutExtension }
            ?.toSet()
            ?: emptySet()
    }

    override suspend fun getDownloadedFilePath(bookId: String): String? = withContext(Dispatchers.IO) {
        File(context.getDownloadDir(), fileNameFor(bookId)).takeIf { it.exists() }?.absolutePath
    }

    override suspend fun deleteDownload(bookId: String) {
        withContext(Dispatchers.IO) {
            File(context.getDownloadDir(), fileNameFor(bookId)).delete()
        }
        bookReaderLinkDao.delete(bookId)
    }

    override suspend fun getReaderId(bookId: String): Long? = bookReaderLinkDao.getReaderId(bookId)

    override suspend fun saveReaderId(bookId: String, readerId: Long) {
        bookReaderLinkDao.upsert(BookReaderLinkEntity(bookId, readerId))
    }

    companion object {
        private const val EPUB_EXTENSION = "epub"
        private fun fileNameFor(bookId: String) = "$bookId.$EPUB_EXTENSION"
    }
}
