package ru.akhilko.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.result.Result
import ru.akhilko.core.result.asResult
import ru.akhilko.month.navigation.CENTERED_MONTH
import ru.akhilko.month.navigation.DAY_SELECTED
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calendarDayRepository: CalendarDayRepository,
) : ViewModel() {

    private val centeredMonth: StateFlow<YearMonth> = savedStateHandle.getStateFlow(
        CENTERED_MONTH,
        YearMonth.now()
    )

    private val daySelected: StateFlow<String?> = savedStateHandle.getStateFlow(
        DAY_SELECTED,
        LocalDate.now().dayOfMonth.toString()
    )

    init {
        viewModelScope.launch {
            calendarDayRepository.sync()
        }
    }

    val monthUiState: StateFlow<MonthUiState> =
        combine(centeredMonth, daySelected) { month, day ->
            month to day
        }.flatMapLatest { (yearMonth, selectedDay) ->
            calendarDayRepository.getDaysByMonth(yearMonth.year, yearMonth.monthValue)
                .asResult()
                .map { result ->
                    when (result) {
                        is Result.Success -> MonthUiState.Success(
                            centeredMonth = yearMonth,
                            daySelected = selectedDay,
                            days = result.data,
                        )

                        is Result.Loading -> MonthUiState.Loading
                        is Result.Error -> MonthUiState.Error
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MonthUiState.Loading,
        )
}

sealed interface MonthUiState {
    data object Loading : MonthUiState

    data class Success(
        val centeredMonth: YearMonth,
        val daySelected: String?,
        val days: List<CalendarDayResource>,
    ) : MonthUiState

    data object Error : MonthUiState
}
