package com.marxist.android.utils

import com.marxist.android.data.model.DownloadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

suspend fun downloadFile(file: File, url: String): Flow<DownloadResult> {
    return flow {
        emit(DownloadResult.Loading)
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()
        try {
            if (response.body != null) {
                val buffer = response.body!!.byteStream()
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    val bufferSize = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = buffer.read(bufferSize)
                        if (byteCount < 0) break
                        output.write(bufferSize, 0, byteCount)
                    }
                    output.flush()
                }
            }

            emit(DownloadResult.Success)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DownloadResult.Error("File not downloaded"))
        }
    }
}