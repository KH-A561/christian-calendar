package ru.akhilko.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.core.database.entity.month.MonthSummaryEntity
import ru.akhilko.core.database.repository.CalendarRepository
import ru.akhilko.core.result.Result
import ru.akhilko.core.result.asResult
import ru.akhilko.month.navigation.CENTERED_MONTH
import ru.akhilko.month.navigation.DAY_SELECTED
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    calendarRepository: CalendarRepository,
) : ViewModel() {
    private val today = LocalDate.now()!!

    private val centeredMonth: StateFlow<YearMonth?> = savedStateHandle.getStateFlow(
        CENTERED_MONTH,
        YearMonth.now()
    )

    private val daySelected: StateFlow<String?> = savedStateHandle.getStateFlow(
        DAY_SELECTED,
        today.dayOfMonth.toString()
    )

    val monthUiState: StateFlow<MonthUiState> = monthUiState(
        centeredMonth.value,
        daySelected.value,
        calendarRepository
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MonthUiState.Loading,
        )
}

private fun monthUiState(
    centeredMonth: YearMonth?,
    daySelected: String?,
    calendarRepository: CalendarRepository
): Flow<MonthUiState> {
    val monthsInfo = calendarRepository.getMonthsInfo(
        false, emptySet(), centeredMonth?.year ?: Year.now().value
    )
    return monthsInfo.asResult().map { monthsResult ->
        when (monthsResult) {
            is Result.Success -> {
                MonthUiState.MonthsLoaded(
                    centeredMonth = centeredMonth,
                    daySelected = daySelected,
                    monthSummary = monthsResult.data
                )
            }

            is Result.Loading -> MonthUiState.Loading
            is Result.Error -> MonthUiState.Empty
        }
    }
}

sealed interface MonthUiState {
    data object Loading : MonthUiState

    data class MonthsLoaded(
        val centeredMonth: YearMonth?,
        val daySelected: String?,
        val monthSummary: List<MonthSummaryEntity>
    ) : MonthUiState

    data object Empty : MonthUiState
}