package org.cpimtn.marxist.android.app

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.cpimtn.marxist.android.domain.model.AppLanguage

object LocaleController {

    /** Returns true if the locale actually changed (i.e. the UI needs to be recreated). */
    fun apply(language: AppLanguage) {
        Log.d("Khaleel", "language: $language")
        val tag = when (language) {
            AppLanguage.TAMIL -> "ta"
            AppLanguage.ENGLISH -> "en"
        }
        Log.d("Khaleel", "tag: $tag")
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
