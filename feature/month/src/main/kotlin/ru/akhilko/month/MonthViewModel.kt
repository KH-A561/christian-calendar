package ru.akhilko.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.result.Result
import ru.akhilko.core.result.asResult
import ru.akhilko.month.navigation.DAY_SELECTED
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val calendarDayRepository: CalendarDayRepository,
) : ViewModel() {

    // Месяц, который сейчас видит пользователь (для заголовка в TopAppBar)
    private val _currentVisibleMonth = MutableStateFlow(YearMonth.now())
    val currentVisibleMonth: StateFlow<YearMonth> = _currentVisibleMonth

    // Канал для отправки одноразовых событий, таких как прокрутка
    private val _scrollToTodayChannel = Channel<Unit>(Channel.CONFLATED)
    val scrollToTodayRequested = _scrollToTodayChannel.receiveAsFlow()

    private val daySelected: StateFlow<String?> = savedStateHandle.getStateFlow(
        DAY_SELECTED,
        LocalDate.now().dayOfMonth.toString()
    )

    init {
        viewModelScope.launch {
            calendarDayRepository.sync()
        }
    }

    fun onTodayClick() {
        viewModelScope.launch {
            _scrollToTodayChannel.send(Unit)
        }
    }

    fun onVisibleMonthChanged(yearMonth: YearMonth) {
        _currentVisibleMonth.value = yearMonth
    }

    val monthUiState: StateFlow<MonthUiState> =
        combine(_currentVisibleMonth, daySelected) { month, day ->
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
