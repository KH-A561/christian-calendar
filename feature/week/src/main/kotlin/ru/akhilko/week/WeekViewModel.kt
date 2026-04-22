package ru.akhilko.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.core.ui.mapper.toPresentation
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

sealed interface WeekUiState {
    data object Loading : WeekUiState
    data class Success(
        val weekStart: LocalDate,
        val days: List<CalendarDayPresentation>,
    ) : WeekUiState
    data object Error : WeekUiState
}

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
) : ViewModel() {

    private val _weekStart = MutableStateFlow(currentWeekStart())

    private val weekDaysFlow = _weekStart.flatMapLatest { weekStart ->
        calendarDayRepository
            .getDaysByIds(weekDayIds(weekStart))
            .map { resources ->
                val today = LocalDate.now()
                val resourcesById = resources.associateBy { it.id }
                weekDayIds(weekStart).mapNotNull { id ->
                    resourcesById[id]?.day?.toPresentation(today)
                }
            }
    }

    val uiState: StateFlow<WeekUiState> = weekDaysFlow
        .map { days ->
            WeekUiState.Success(
                weekStart = _weekStart.value,
                days = days,
            ) as WeekUiState
        }
        .catch {
            emit(WeekUiState.Error)
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeekUiState.Loading,
    )

    fun prevWeek() {
        _weekStart.value = _weekStart.value.minusWeeks(1)
    }

    fun nextWeek() {
        _weekStart.value = _weekStart.value.plusWeeks(1)
    }

    fun resetToCurrentWeek() {
        _weekStart.value = currentWeekStart()
    }

    private fun currentWeekStart(): LocalDate =
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun weekDayIds(weekStart: LocalDate): List<String> =
        (0..6).map { offset -> weekStart.plusDays(offset.toLong()).toString() }
}
