package org.cpimtn.marxist.android.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.cpimtn.marxist.android.domain.model.AppLanguage

object LocaleController {

    fun apply(language: AppLanguage) {
        val tag = when (language) {
            AppLanguage.TAMIL -> "ta"
            AppLanguage.ENGLISH -> "en"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
