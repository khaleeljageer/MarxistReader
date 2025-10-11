package com.jskaleel.android.network.downloader

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import okhttp3.OkHttpClient
import okhttp3.Request
import com.jskaleel.android.network.model.DownloadResult
import java.io.File
import java.io.IOException
import java.net.URL
import javax.inject.Inject

interface FileDownloader {
    suspend fun downloadFile(
        url: String,
        uniqueId: String,
        fileName: String,
        destinationPath: String,
        coroutineScope: CoroutineScope
    ): Flow<DownloadResult>
}

class FileDownloaderImpl @Inject constructor(
    private val client: OkHttpClient
) : FileDownloader {

    override suspend fun downloadFile(
        url: String,
        uniqueId: String,
        fileName: String,
        destinationPath: String,
        coroutineScope: CoroutineScope
    ): Flow<DownloadResult> = flow {
        if (!isValidUrl(url)) {
            emit(DownloadResult.Error(id = uniqueId, message = "Insecure or invalid URL"))
            return@flow
        }
        emit(DownloadResult.Queued(id = uniqueId))
        val destinationFile = File(destinationPath)

        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                emit(
                    DownloadResult.Error(
                        id = uniqueId,
                        message = "Unexpected response ${response.code}"
                    )
                )
                return@flow
            }

            val body = response.body ?: throw IOException("Response body is null")

            val input = body.byteStream()
            val output = destinationFile.outputStream()
            var bytesRead: Int
            var totalBytesRead: Long = 0
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            while (input.read(buffer).also { bytesRead = it } != -1) {
                if (!coroutineScope.isActive) {
                    throw CancellationException("Download was cancelled")
                }
                output.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
            }

            output.flush()
            output.close()
            input.close()
            emit(DownloadResult.Success(id = uniqueId, title = fileName, file = destinationFile))
        } catch (e: CancellationException) {
            destinationFile.delete()
            throw e
        } catch (e: Exception) {
            destinationFile.delete()
            emit(DownloadResult.Error(id = uniqueId, e.message.orEmpty()))
        }
    }.flowOn(Dispatchers.IO)

    private fun isValidUrl(url: String): Boolean {
        return try {
            val parsedUrl = URL(url)
            parsedUrl.protocol.equals("https", ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
    }
}
