package com.marxist.android.utils

import com.marxist.android.data.model.DownloadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

suspend fun OkHttpClient.downloadFile(file: File, url: String): Flow<DownloadResult> {
    return flow {
        emit(DownloadResult.Loading)
        val request = Request.Builder().url(url).build()
        val response = this@downloadFile.newCall(request).execute()

        val data = ByteArray(response.body?.contentLength()!!.toInt())
        response.close()
        if (response.isSuccessful) {
            file.writeBytes(data)
            emit(DownloadResult.Success)
        } else {
            emit(DownloadResult.Error("File not downloaded"))
        }
    }
}