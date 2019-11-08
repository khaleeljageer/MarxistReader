package com.marxist.android.utils.download

import android.content.Context
import android.content.SharedPreferences
import com.marxist.android.BuildConfig
import com.marxist.android.utils.AppConstants

object PreferencesHelper {

    const val BOOKS_KEY = "${BuildConfig.APPLICATION_ID}.books"
    const val AUDIO_KEY = "${BuildConfig.APPLICATION_ID}.audio"

    private fun get(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppConstants.SharedPreference.FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveAudioDownload(context: Context, id: Long) {
        val preferences = get(context)

        val downloads = preferences.getStringSet(AUDIO_KEY, HashSet<String>())
        downloads?.add(id.toString())

        val editor = preferences.edit()
        editor.putStringSet(AUDIO_KEY, downloads)
        editor.apply()
    }

    fun saveBooksDownload(context: Context, id: Long) {
        val preferences = get(context)

        val downloads = preferences.getStringSet(BOOKS_KEY, HashSet<String>())
        downloads?.add(id.toString())

        val editor = preferences.edit()
        editor.putStringSet(BOOKS_KEY, downloads)
        editor.apply()
    }

    fun getAudioDownloads(context: Context): Set<Long> {

        val longs = HashSet<Long>()

        val preferences = get(context)
        val downloads = preferences.getStringSet(AUDIO_KEY, emptySet())
        downloads?.forEach { it ->
            longs.add(it.toLong())
        }

        return longs
    }

    fun getBooksDownloads(context: Context): Set<Long> {
        val longs = HashSet<Long>()

        val preferences = get(context)
        val downloads = preferences.getStringSet(BOOKS_KEY, emptySet())
        downloads?.forEach { it ->
            longs.add(it.toLong())
        }

        return longs
    }

    fun clearAudioDownloads(context: Context) {
        val editor = get(context).edit()
        editor.remove(AUDIO_KEY)
        editor.apply()
    }

    fun clearBooksDownloads(context: Context) {
        val editor = get(context).edit()
        editor.remove(BOOKS_KEY)
        editor.apply()
    }
}