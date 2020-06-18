package com.marxist.android.model

data class NetWorkMessage(var message: String, val type: ConnectivityType)
data class ShowSnackBar(var message: String)
data class DarkModeChanged(var message: String)

data class FontChange(var fontId: Int)
data class FontSizeChange(var fontSize: Float)
data class ReaderBgChange(var color: Long)
