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
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.core.ui.mapper.toPresentation
import ru.akhilko.core.ui.state.SelectedDayHolder
import ru.akhilko.day.navigation.DAY_ID_SAVED_STATE_KEY
import java.time.LocalDate
import javax.inject.Inject

sealed interface DayScreenUiState {
    data object Loading : DayScreenUiState
    data class Success(
        val day: CalendarDayPresentation,
        val prevId: String,
        val nextId: String,
    ) : DayScreenUiState
    data object Error : DayScreenUiState
}

@HiltViewModel
class DayViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
    selectedDayHolder: SelectedDayHolder,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dayId: String = checkNotNull(savedStateHandle[DAY_ID_SAVED_STATE_KEY])

    init {
        // Запоминаем последний просмотренный день — для вкладки DAY в нижней навигации.
        selectedDayHolder.setSelectedDay(dayId)
    }

    val uiState: StateFlow<DayScreenUiState> = calendarDayRepository
        .getDay(dayId)
        .map { resource ->
            if (resource != null) {
                val presentation = resource.day.toPresentation()
                val gregorian = LocalDate.of(
                    resource.day.gregorianYear,
                    resource.day.gregorianMonth,
                    resource.day.gregorianDay,
                )
                DayScreenUiState.Success(
                    day = presentation,
                    prevId = gregorian.minusDays(1).toString(),
                    nextId = gregorian.plusDays(1).toString(),
                )
            } else {
                DayScreenUiState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayScreenUiState.Loading,
        )
}
