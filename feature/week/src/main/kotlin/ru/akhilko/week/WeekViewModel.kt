package ru.akhilko.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.data.repository.SearchContentsRepository
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.core.ui.mapper.toPresentation
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import java.time.temporal.TemporalAdjusters

enum class WeekMode { LIST, SEARCH }

sealed interface WeekUiState {
    data object Loading : WeekUiState
    data class Success(
        val weekStart: LocalDate,
        val days: List<CalendarDayPresentation>,
        val mode: WeekMode,
        val query: String,
        val filter: BadgeKind?,
        val results: List<CalendarDayPresentation>
    ) : WeekUiState
    data object Error : WeekUiState
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class WeekViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
    private val searchContentsRepository: SearchContentsRepository
) : ViewModel() {

    private val _weekStart = MutableStateFlow(getCurrentIsoWeekStart())
    private val _mode = MutableStateFlow(WeekMode.LIST)
    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow<BadgeKind?>(null)

    val uiState: StateFlow<WeekUiState> = combine(
        _weekStart,
        _mode,
        _query,
        _filter
    ) { weekStart, mode, query, filter ->
        WeekStateParams(weekStart, mode, query, filter)
    }
    .flatMapLatest { params ->
        val weekDaysFlow = calendarDayRepository.getDaysByIds(
            getDaysIdsForWeek(params.weekStart)
        ).map { resources ->
            resources.map { it.day.toPresentation() }
        }

        val searchFlow = if (params.mode == WeekMode.SEARCH && params.query.isNotBlank()) {
            flowOf(params.query)
                .debounce(250)
                .flatMapLatest { q ->
                    searchContentsRepository.searchContents(q)
                        .map { searchResult ->
                            searchResult.dayData
                                .map { it.day.toPresentation() }
                                .filter { presentation ->
                                    params.filter == null || presentation.badges.contains(params.filter)
                                }
                        }
                }
        } else {
            flowOf(emptyList())
        }

        combine(weekDaysFlow, searchFlow) { weekDays, searchResults ->
            if (weekDays.isEmpty() && params.mode == WeekMode.LIST) {
                WeekUiState.Error
            } else {
                WeekUiState.Success(
                    weekStart = params.weekStart,
                    days = weekDays.sortedBy { LocalDate.parse(it.id) },
                    mode = params.mode,
                    query = params.query,
                    filter = params.filter,
                    results = searchResults
                )
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeekUiState.Loading
    )

    fun prevWeek() {
        _weekStart.update { it.minusWeeks(1) }
    }

    fun nextWeek() {
        _weekStart.update { it.plusWeeks(1) }
    }

    fun toggleMode() {
        _mode.update { if (it == WeekMode.LIST) WeekMode.SEARCH else WeekMode.LIST }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun onFilterSelected(filter: BadgeKind?) {
        _filter.value = filter
    }

    fun resetToCurrentWeek() {
        _weekStart.value = getCurrentIsoWeekStart()
    }

    private fun getCurrentIsoWeekStart(): LocalDate {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    private fun getDaysIdsForWeek(weekStart: LocalDate): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return (0..6).map { i ->
            weekStart.plusDays(i.toLong()).format(formatter)
        }
    }

    private data class WeekStateParams(
        val weekStart: LocalDate,
        val mode: WeekMode,
        val query: String,
        val filter: BadgeKind?
    )
}
