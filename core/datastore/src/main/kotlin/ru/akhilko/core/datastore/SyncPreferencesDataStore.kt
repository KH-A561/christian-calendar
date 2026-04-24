package ru.akhilko.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "sync_preferences"

private val Context.syncPreferencesDataStore by preferencesDataStore(name = STORE_NAME)

@Singleton
class SyncPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun getLastSyncTimestamp(): Long {
        return context.syncPreferencesDataStore.data
            .map { preferences -> preferences[LAST_SYNC_TIMESTAMP_KEY] ?: 0L }
            .first()
    }

    suspend fun updateLastSyncTimestamp(time: Long) {
        context.syncPreferencesDataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP_KEY] = time
        }
    }

    private companion object {
        val LAST_SYNC_TIMESTAMP_KEY = longPreferencesKey("last_sync_timestamp")
    }
}
