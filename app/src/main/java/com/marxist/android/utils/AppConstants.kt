package com.marxist.android.utils

object AppConstants {
    const val AUDIO = "audio"
    const val BOOKS = "books"
    val NIGHT_MODE: Boolean
        get() = false
    val INSTANCE_NEWS: Boolean?
        get() = true
    const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

    object SharedPreference {
        val FILE_NAME: String
            get() = "com.marxist.android.PREFERENCE"
        val INSTANCE_NEWS_STATUS: String
            get() = "com.marxist.android.INSTANCE_NEWS_STATUS"
    }
}