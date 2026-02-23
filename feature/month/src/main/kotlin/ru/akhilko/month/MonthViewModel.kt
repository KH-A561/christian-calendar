package ru.akhilko.month

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.domain.GenerateMonthSummariesUseCase
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import java.time.YearMonth
import javax.inject.Inject

@Immutable
sealed interface MonthScreenUiState {
    data object Loading : MonthScreenUiState
    data class Success(
        val days: List<CalendarDayResource>,
        val summaries: Map<Int, List<MonthSummary>>,
    ) : MonthScreenUiState

    data object Error : MonthScreenUiState
}

@HiltViewModel
class MonthViewModel @Inject constructor(
    private val calendarDayRepository: CalendarDayRepository,
    private val generateMonthSummariesUseCase: GenerateMonthSummariesUseCase,
) : ViewModel() {

    private val _scrollToTodayChannel = Channel<Unit>(Channel.CONFLATED)
    val scrollToTodayRequested: Flow<Unit> = _scrollToTodayChannel.receiveAsFlow()

    private val syncedYears = mutableSetOf<Int>()

    private val _summaries = MutableStateFlow<Map<Int, List<MonthSummary>>>(emptyMap())

    val uiState: StateFlow<MonthScreenUiState> = combine(
        calendarDayRepository.getAll(),
        _summaries
    ) { days, summaries ->
        MonthScreenUiState.Success(days = days, summaries = summaries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MonthScreenUiState.Loading,
    )

    init {
        onVisibleYearChanged(YearMonth.now().year)
    }

    fun onVisibleYearChanged(year: Int) {
        if (year !in syncedYears) {
            syncedYears.add(year)
            viewModelScope.launch {
                calendarDayRepository.sync(year)
                (uiState.value as? MonthScreenUiState.Success)?.let { currentState ->
                    val newSummaries = generateMonthSummariesUseCase(currentState.days, year)
                    val updatedSummaries = _summaries.value.toMutableMap()
                    updatedSummaries[year] = newSummaries
                    _summaries.value = updatedSummaries
                }
            }
        }
    }

    fun onTodayClick() {
        viewModelScope.launch {
            _scrollToTodayChannel.send(Unit)
        }
    }
}
