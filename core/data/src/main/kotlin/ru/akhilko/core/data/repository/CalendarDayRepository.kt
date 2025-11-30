package ru.akhilko.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource

interface CalendarDayRepository {

    fun getAll(): Flow<List<CalendarDayResource>>

    fun getDaysByMonth(
        year: Int, month: Int
    ): Flow<List<CalendarDayResource>>

    fun getDaysByIds(
        ids: List<String>
    ): Flow<List<CalendarDayResource>>

    suspend fun sync()
}
