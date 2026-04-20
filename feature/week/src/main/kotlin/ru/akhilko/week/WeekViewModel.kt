package ru.akhilko.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.data.repository.SearchContentsRepository
import ru.akhilko.core.designsystem.component.BadgeKind
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
        val mode: WeekMode,
        val query: String,
        val filter: BadgeKind?,
        val results: List<CalendarDayPresentation>,
    ) : WeekUiState
    data object Error : WeekUiState
}

enum class WeekMode {
    LIST,
    SEARCH,
}

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
    private val searchContentsRepository: SearchContentsRepository,
) : ViewModel() {

    private val _weekStart = MutableStateFlow(currentWeekStart())
    private val _mode = MutableStateFlow(WeekMode.LIST)
    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow<BadgeKind?>(null)

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

    private val searchResultsFlow = combine(
        _query.debounce(250),
        _filter,
    ) { query, filter ->
        query.trim() to filter
    }.flatMapLatest { (query, filter) ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            searchContentsRepository
                .searchContents(query)
                .map { result ->
                    val today = LocalDate.now()
                    result.dayData
                        .map { it.day.toPresentation(today) }
                        .filterByBadge(filter)
                }
        }
    }

    private data class WeekContext(
        val weekStart: LocalDate,
        val days: List<CalendarDayPresentation>,
        val mode: WeekMode,
        val query: String,
        val filter: BadgeKind?,
    )

    private val weekContextFlow = combine(
        _weekStart,
        weekDaysFlow,
        _mode,
        _query,
        _filter,
    ) { weekStart, days, mode, query, filter ->
        WeekContext(
            weekStart = weekStart,
            days = days,
            mode = mode,
            query = query,
            filter = filter,
        )
    }

    val uiState: StateFlow<WeekUiState> = combine(
        weekContextFlow,
        searchResultsFlow,
    ) { context, results ->
        WeekUiState.Success(
            weekStart = context.weekStart,
            days = context.days,
            mode = context.mode,
            query = context.query,
            filter = context.filter,
            results = results,
        ) as WeekUiState
    }.catch {
        emit(WeekUiState.Error)
    }.stateIn(
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

    fun toggleMode() {
        _mode.value = when (_mode.value) {
            WeekMode.LIST -> WeekMode.SEARCH
            WeekMode.SEARCH -> WeekMode.LIST
        }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun onFilterSelected(filter: BadgeKind?) {
        _filter.value = filter
    }

    private fun currentWeekStart(): LocalDate =
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun weekDayIds(weekStart: LocalDate): List<String> =
        (0..6).map { offset -> weekStart.plusDays(offset.toLong()).toString() }
}

private fun List<CalendarDayPresentation>.filterByBadge(
    filter: BadgeKind?,
): List<CalendarDayPresentation> {
    if (filter == null) return this
    return filter { presentation -> filter in presentation.badges }
}
