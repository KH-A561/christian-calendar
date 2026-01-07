package ru.akhilko.day

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.result.Result
import ru.akhilko.core.result.asResult
import ru.akhilko.day.navigation.DAY_ID_SAVED_STATE_KEY
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    calendarDayRepository: CalendarDayRepository,
) : ViewModel() {

    private val dayId: StateFlow<String> = savedStateHandle.getStateFlow(
        DAY_ID_SAVED_STATE_KEY, "")

    val dayUiState: StateFlow<DayUiState> = dayId.flatMapLatest { id ->
        calendarDayRepository.getDaysByIds(listOf(id))
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val dayResource = result.data.firstOrNull()
                        if (dayResource != null) {
                            DayUiState.Success(dayResource)
                        } else {
                            DayUiState.Error
                        }
                    }
                    is Result.Loading -> DayUiState.Loading
                    is Result.Error -> DayUiState.Error
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DayUiState.Loading,
    )
}

sealed interface DayUiState {
    data object Loading : DayUiState
    data class Success(val day: CalendarDayResource) : DayUiState
    data object Error : DayUiState
}
