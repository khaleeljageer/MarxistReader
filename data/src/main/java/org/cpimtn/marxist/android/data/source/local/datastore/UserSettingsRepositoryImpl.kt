package org.cpimtn.marxist.android.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.AppVersionProvider
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.android.domain.model.UserSettings
import org.cpimtn.marxist.android.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

private val KEY_THEME = stringPreferencesKey("theme")
private val KEY_FONT_SIZE = stringPreferencesKey("font_size")
private val KEY_LANGUAGE = stringPreferencesKey("language")
private val KEY_PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications_enabled")
private val KEY_WELCOME_COMPLETED = booleanPreferencesKey("welcome_completed")
private val KEY_SEEN_HELP_TOPICS = stringSetPreferencesKey("seen_help_topics")

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appVersionProvider: AppVersionProvider,
) : SettingsRepository {

    override fun getWelcomeCompleted(): Flow<Boolean> =
        context.userSettingsDataStore.data.map { prefs ->
            prefs[KEY_WELCOME_COMPLETED] ?: false
        }

    override fun getSeenHelpTopics(): Flow<Set<String>> =
        context.userSettingsDataStore.data.map { prefs ->
            prefs[KEY_SEEN_HELP_TOPICS] ?: emptySet()
        }

    override fun getSettings(): Flow<UserSettings> =
        context.userSettingsDataStore.data.map { prefs ->
            UserSettings(
                theme = prefs[KEY_THEME]?.let { Theme.valueOf(it) }
                    ?: Theme.DEFAULT,
                fontSize = prefs[KEY_FONT_SIZE]?.let { FontSize.valueOf(it) }
                    ?: FontSize.NORMAL,
                language = prefs[KEY_LANGUAGE]?.let { AppLanguage.valueOf(it) }
                    ?: AppLanguage.TAMIL,
                pushNotificationsEnabled = prefs[KEY_PUSH_NOTIFICATIONS] ?: true,
                appVersion = appVersionProvider.getVersion(),
            )
        }

    override suspend fun setTheme(theme: Theme) {
        context.userSettingsDataStore.edit { it[KEY_THEME] = theme.name }
    }

    override suspend fun setFontSize(fontSize: FontSize) {
        context.userSettingsDataStore.edit { it[KEY_FONT_SIZE] = fontSize.name }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.userSettingsDataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    override suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[KEY_PUSH_NOTIFICATIONS] = enabled }
    }

    override suspend fun setWelcomeCompleted(completed: Boolean) {
        context.userSettingsDataStore.edit { it[KEY_WELCOME_COMPLETED] = completed }
    }

    override suspend fun markHelpSeen(topic: String) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[KEY_SEEN_HELP_TOPICS] = (prefs[KEY_SEEN_HELP_TOPICS] ?: emptySet()) + topic
        }
    }
}
