package com.marxist.android.utils.download

import android.content.Context
import androidx.core.content.ContextCompat
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil
import com.marxist.android.R

object DownloadUtil {
    fun openSavedBook(context: Context, path: String) {
        var config = AppUtil.getSavedConfig(context)
        if (config == null) config = Config()
        config.allowedDirection = Config.AllowedDirection.ONLY_VERTICAL
        config.isShowTts = false
        config.isNightMode = false
        config.font = R.font.hind_regular
        config.fontSize = 3
        config.setThemeColorInt(ContextCompat.getColor(context, R.color.primary_red))
        val folioReader = FolioReader.get()
        folioReader.setConfig(config, true).openBook(path)
    }
}