package ru.akhilko.christian_calendar.core.domain

import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.domain.model.HighlightedDay
import ru.akhilko.christian_calendar.core.domain.model.HighlightedPeriod
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import ru.akhilko.christian_calendar.core.model.DayType
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

            // Единичные события месяца:
            // - EASTER / TWELVE_GREAT_FEASTS / GREAT_FEAST / COMMEMORATION
            // - FAST только если есть fastingName
            daysInMonth.forEach { day ->
                val dayTypes = day.day.dayTypes
                val hasSingleEventType = dayTypes.any {
                    it == DayType.EASTER ||
                        it == DayType.TWELVE_GREAT_FEASTS ||
                        it == DayType.GREAT_FEAST ||
                        it == DayType.COMMEMORATION
                }
                val hasFastWithName = dayTypes.contains(DayType.FAST) &&
                    !day.day.fastingInfo.fastingName.isNullOrBlank()

                if (hasSingleEventType || hasFastWithName) {
                    val eventName = day.day.fastingInfo.fastingName
                        ?.takeIf { hasFastWithName }
                        ?: day.day.title
                    if (eventName.isNotBlank()) {
                        highlightedDays.add(
                            HighlightedDay(
                                dayOfMonth = day.day.gregorianDay,
                                name = eventName,
                            )
                        )
                    }
                }
            }

            // LONG_FAST — только периодами "от-до".
            val longFastDaysByName = daysInMonth
                .filter { it.day.dayTypes.contains(DayType.LONG_FAST) }
                .groupBy { resource ->
                    resource.day.fastingInfo.fastingName
                        ?.takeIf { it.isNotBlank() }
                        ?: resource.day.title.ifBlank { "Пост" }
                }

            longFastDaysByName.forEach { (name, fastDays) ->
                val sortedDays = fastDays.sortedBy { it.day.gregorianDay }
                if (sortedDays.isEmpty()) return@forEach

                var rangeStart = sortedDays.first().day.gregorianDay
                var previousDay = rangeStart

                for (index in 1 until sortedDays.size) {
                    val currentDay = sortedDays[index].day.gregorianDay
                    val isContinuous = currentDay == previousDay + 1
                    if (!isContinuous) {
                        highlightedPeriods.add(
                            HighlightedPeriod(
                                startDay = rangeStart,
                                endDay = previousDay,
                                name = name,
                            )
                        )
                        rangeStart = currentDay
                    }
                    previousDay = currentDay
                }

                highlightedPeriods.add(
                    HighlightedPeriod(
                        startDay = rangeStart,
                        endDay = previousDay,
                        name = name,
                    )
                )
            }

            monthSummaries.add(
                MonthSummary(
                    year = year,
                    month = month,
                    highlightedDays = highlightedDays
                        .distinctBy { it.dayOfMonth to it.name }
                        .sortedBy { it.dayOfMonth },
                    highlightedPeriods = highlightedPeriods
                        .sortedBy { it.startDay },
                )
            )
        }

        return monthSummaries
    }
}
