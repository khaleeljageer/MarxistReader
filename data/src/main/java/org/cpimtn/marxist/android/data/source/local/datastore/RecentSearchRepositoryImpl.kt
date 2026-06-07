package org.cpimtn.marxist.android.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.repository.RecentSearchRepository
import javax.inject.Inject
import javax.inject.Singleton

private val Context.recentSearchDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_search")

private val KEY_RECENT_QUERIES = stringPreferencesKey("recent_queries")
private const val DELIMITER = "|"

@Singleton
class RecentSearchRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : RecentSearchRepository {

    override fun getRecentSearches(maxSize: Int): Flow<List<String>> =
        context.recentSearchDataStore.data.map { prefs ->
            prefs[KEY_RECENT_QUERIES]
                ?.split(DELIMITER)
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.take(maxSize)
                ?: emptyList()
        }

    override suspend fun addRecentSearch(query: String, maxSize: Int) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        context.recentSearchDataStore.edit { prefs ->
            val current = prefs[KEY_RECENT_QUERIES]
                ?.split(DELIMITER)
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()
            val updated = listOf(trimmed) + current.filter { it.equals(trimmed, ignoreCase = true).not() }
            prefs[KEY_RECENT_QUERIES] = updated.take(maxSize).joinToString(DELIMITER)
        }
    }

    override suspend fun clearRecentSearches() {
        context.recentSearchDataStore.edit { it.remove(KEY_RECENT_QUERIES) }
    }
}
