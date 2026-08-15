package org.cpimtn.marxist.android.domain.model

data class UserSettings(
    val theme: Theme = Theme.DEFAULT,
    val fontSize: FontSize = FontSize.NORMAL,
    val language: AppLanguage = AppLanguage.TAMIL,
    val pushNotificationsEnabled: Boolean = true,
    val helpIconVisible: Boolean = true,
    val appVersion: String = "6.0.0"
)
