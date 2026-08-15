package org.cpimtn.marxist.android.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.android.domain.model.UserSettings

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    fun getWelcomeCompleted(): Flow<Boolean>
    /** Names of [org.cpimtn.marxist.android.domain.model.HelpTopic] whose help has been shown. */
    fun getSeenHelpTopics(): Flow<Set<String>>

    /** How many articles the user has opened; drives the one-time review prompt. */
    fun getArticleReadCount(): Flow<Int>

    /** Whether the review prompt has already been offered — it is only ever shown once. */
    fun getReviewPromptShown(): Flow<Boolean>
    suspend fun setTheme(theme: Theme)
    suspend fun setFontSize(fontSize: FontSize)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setPushNotificationsEnabled(enabled: Boolean)
    suspend fun setHelpIconVisible(visible: Boolean)
    suspend fun setWelcomeCompleted(completed: Boolean)
    suspend fun markHelpSeen(topic: String)
    suspend fun incrementArticleReadCount()
    suspend fun setReviewPromptShown()
}
