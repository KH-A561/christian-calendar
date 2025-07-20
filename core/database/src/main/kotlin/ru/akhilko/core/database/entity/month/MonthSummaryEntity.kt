package ru.akhilko.core.database.entity.month

import ru.akhilko.christian_calendar.core.data.model.DaySummary
import ru.akhilko.christian_calendar.core.model.DayType
import java.time.YearMonth

data class MonthSummaryEntity (
    val yearMonth: YearMonth,
    val presentDayTypes: Set<DayType>,
    val featuredDays: List<DaySummary>
)