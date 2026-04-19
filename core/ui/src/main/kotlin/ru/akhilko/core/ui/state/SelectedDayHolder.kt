package ru.akhilko.core.ui.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хранит id последнего просмотренного дня (формат YYYY-MM-DD).
 * Используется вкладкой DAY нижней навигации, чтобы открывать тот день, который
 * пользователь смотрел последним, а не «сегодня».
 */
@Singleton
class SelectedDayHolder @Inject constructor() {

    private val _selectedDayId = MutableStateFlow(LocalDate.now().toString())
    val selectedDayId: StateFlow<String> = _selectedDayId.asStateFlow()

    fun setSelectedDay(id: String) {
        _selectedDayId.value = id
    }
}
