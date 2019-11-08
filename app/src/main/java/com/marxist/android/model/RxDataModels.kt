package com.marxist.android.model

data class NetWorkMessage(var message: String, val type: ConnectivityType)
data class ShowSnackBar(var message: String)
data class ShareSnackBar(var message: String, var title: String, var extra: String)
data class DarkModeChanged(var message: String)