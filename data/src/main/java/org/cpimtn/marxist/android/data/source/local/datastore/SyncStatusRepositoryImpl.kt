package org.cpimtn.marxist.android.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.cpimtn.marxist.android.domain.model.SyncStatus
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_status")

private val KEY_LAST_SYNCED_AT = longPreferencesKey("last_synced_at")
private val KEY_LAST_ERROR = stringPreferencesKey("last_error")

@Singleton
class SyncStatusRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncStatusRepository {

    private val _isSyncing = MutableStateFlow(false)

    private val persistedFlow: Flow<SyncStatus> = context.syncDataStore.data.map { prefs ->
        SyncStatus(
            isSyncing = false,
            lastSyncedAtMillis = prefs[KEY_LAST_SYNCED_AT].takeIf { it != null && it > 0L },
            lastError = prefs[KEY_LAST_ERROR],
        )
    }

    override fun getSyncStatus(): Flow<SyncStatus> = combine(
        _isSyncing,
        persistedFlow,
    ) { syncing, persisted ->
        persisted.copy(isSyncing = syncing)
    }

    override suspend fun setSyncing(syncing: Boolean) {
        _isSyncing.value = syncing
    }

    override suspend fun recordSyncSuccess() {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_LAST_SYNCED_AT] = System.currentTimeMillis()
            prefs.remove(KEY_LAST_ERROR)
        }
        _isSyncing.value = false
    }

    override suspend fun recordSyncError(message: String) {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_LAST_ERROR] = message
        }
        _isSyncing.value = false
    }
}
