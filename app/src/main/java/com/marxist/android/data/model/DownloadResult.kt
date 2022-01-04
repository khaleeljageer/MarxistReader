package com.marxist.android.data.model

sealed class DownloadResult {
    object Success : DownloadResult()
    object Loading : DownloadResult()
    data class Error(val message: String, val cause: Exception? = null) : DownloadResult()
}