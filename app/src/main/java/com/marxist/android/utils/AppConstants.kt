package com.marxist.android.utils

object AppConstants {
    const val ASPECT_RATIO: Double = 0.5625
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
        val PAGED_INDEX: String
            get() = "com.marxist.android.PAGED_INDEX"
        val INSTANCE_NEWS_STATUS: String
            get() = "com.marxist.android.INSTANCE_NEWS_STATUS"
    }


    const val DOWNLOAD_COMPLETE_PERCENT = 100
    const val MAX_COUNT_OF_SIMULTANEOUS_DOWNLOADS = 2
    const val INVALID_ID = -1

    const val FACEBOOK_SHARE = 1
    const val OTHER_SHARE = 2
}