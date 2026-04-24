package ru.akhilko.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.akhilko.sync.SYNC_WORK_NAME
import ru.akhilko.sync.Sync

private val Context.settingsDataStore by preferencesDataStore(name = "settings")
private val DarkThemeKey = booleanPreferencesKey("dark_theme_enabled")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val isDarkTheme = appContext.settingsDataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { prefs -> prefs[DarkThemeKey] ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isSyncing = WorkManager.getInstance(appContext)
        .getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
        .map { infos -> infos.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun toggleTheme() {
        viewModelScope.launch {
            appContext.settingsDataStore.edit { prefs ->
                val old = prefs[DarkThemeKey] ?: false
                prefs[DarkThemeKey] = !old
            }
        }
    }

    fun triggerSync() {
        Sync.forceSync(appContext)
    }
}
