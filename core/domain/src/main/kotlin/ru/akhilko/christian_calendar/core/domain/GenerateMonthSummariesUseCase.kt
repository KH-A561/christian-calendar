package ru.akhilko.christian_calendar.core.domain

import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.domain.model.HighlightedDay
import ru.akhilko.christian_calendar.core.domain.model.HighlightedPeriod
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import javax.inject.Inject

class GenerateMonthSummariesUseCase @Inject constructor() {

    operator fun invoke(days: List<CalendarDayResource>, year: Int): List<MonthSummary> {
        val monthSummaries = mutableListOf<MonthSummary>()

        for (month in 1..12) {
            val daysInMonth = days.filter {
                it.day.gregorianYear == year && it.day.gregorianMonth == month
            }

            val highlightedDays = mutableListOf<HighlightedDay>()
            val highlightedPeriods = mutableListOf<HighlightedPeriod>()

            // Логика для выделения важных дней
            daysInMonth.forEach { day ->
                if (day.day.liturgicalInfo.importance >= 3) {
                    highlightedDays.add(
                        HighlightedDay(
                            dayOfMonth = day.day.gregorianDay,
                            name = day.day.title
                        )
                    )
                }
            }

            // Логика для группировки постов
            val fastingGroups = daysInMonth
                .filter { it.day.fastingInfo.fastingName != null }
                .groupBy { it.day.fastingInfo.fastingName }

            fastingGroups.forEach { (name, fastingDays) ->
                if (name != null && fastingDays.isNotEmpty()) {
                    val firstDay = fastingDays.minByOrNull { it.day.gregorianDay }!!
                    val lastDay = fastingDays.maxByOrNull { it.day.gregorianDay }!!
                    highlightedPeriods.add(
                        HighlightedPeriod(
                            startDay = firstDay.day.gregorianDay,
                            endDay = lastDay.day.gregorianDay,
                            name = name
                        )
                    )
                }
            }

            monthSummaries.add(
                MonthSummary(
                    year = year,
                    month = month,
                    highlightedDays = highlightedDays,
                    highlightedPeriods = highlightedPeriods
                )
            )
        }

        return monthSummaries
    }
}
