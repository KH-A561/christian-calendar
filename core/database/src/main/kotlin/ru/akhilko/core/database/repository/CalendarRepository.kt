package ru.akhilko.core.database.repository

import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.PopulatedDayResource
import ru.akhilko.core.database.entity.month.MonthSummaryEntity
import java.time.Year

interface CalendarRepository {


    fun getDaysResources(
        useFilterDaysIds: Boolean = false,
        filterDaysIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedDayResource>>

    fun getMonthsInfo(
        useFilterMonths: Boolean = false,
        filterMonths: Set<Int> = emptySet(),
        filterYear: Int = Year.now().value
    ): Flow<List<MonthSummaryEntity>>
}