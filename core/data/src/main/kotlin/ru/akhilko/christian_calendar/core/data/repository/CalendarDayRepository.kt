package ru.akhilko.christian_calendar.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource

interface CalendarDayRepository {
    fun getDay(id: String): Flow<CalendarDayResource?>

    fun getDaysByIds(ids: List<String>): Flow<List<CalendarDayResource>>

    fun getAll(): Flow<List<CalendarDayResource>>

    fun getDaysByMonth(year: Int, month: Int): Flow<List<CalendarDayResource>>

    suspend fun sync()
}
