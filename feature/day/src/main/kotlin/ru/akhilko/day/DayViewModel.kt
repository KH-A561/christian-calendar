package ru.akhilko.day

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.akhilko.christian_calendar.core.data.util.SyncManager
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
) : ViewModel() {
}