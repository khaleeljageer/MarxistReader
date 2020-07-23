package com.marxist.android.utils.download

import android.content.Context
import org.geometerplus.android.fbreader.FBReader

object DownloadUtil {
    fun openSavedBook(context: Context, path: String) {
        FBReader.openBookActivity(context, path)
    }
}