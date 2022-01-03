package com.marxist.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Environment
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.marxist.android.R
import java.io.File
import java.util.*
import java.util.regex.Pattern


object DeviceUtils {

    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun getProgressDisplayLine(
        currentBytes: Long,
        totalBytes: Long
    ): String? {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }

    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun shareIntent(
        subject: String,
        textExtra: String,
        baseContext: Context
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.putExtra(Intent.EXTRA_TEXT, textExtra)

        val shareIntent = Intent.createChooser(
            sendIntent,
            baseContext.getString(R.string.share_text)
        )
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        baseContext.startActivity(shareIntent)
    }

    fun getColor(context: Context): Int = context.resources.getIntArray(R.array.bg_colors).random()
}