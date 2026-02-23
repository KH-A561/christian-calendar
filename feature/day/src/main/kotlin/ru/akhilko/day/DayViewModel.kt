package ru.akhilko.day

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.day.navigation.DAY_ID_SAVED_STATE_KEY
import javax.inject.Inject

sealed interface DayScreenUiState {
    data object Loading : DayScreenUiState
    data class Success(val day: CalendarDay) : DayScreenUiState
    data object Error : DayScreenUiState
}

@HiltViewModel
class DayViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dayId: String = checkNotNull(savedStateHandle[DAY_ID_SAVED_STATE_KEY])

    val uiState: StateFlow<DayScreenUiState> = calendarDayRepository
        .getDay(dayId)
        .map { resource ->
            if (resource != null) {
                DayScreenUiState.Success(resource.day)
            } else {
                DayScreenUiState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayScreenUiState.Loading
        )
}
