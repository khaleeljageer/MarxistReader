package com.marxist.android.model

data class NetWorkMessage(var message: String, val type: ConnectivityType)
data class DarkModeChanged(var message: String)