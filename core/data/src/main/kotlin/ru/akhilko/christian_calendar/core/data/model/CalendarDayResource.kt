package ru.akhilko.christian_calendar.core.data.model

import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.FastingInfo

/**
 * A resource for a calendar day that is ready to be displayed on the screen.
 */
data class CalendarDayResource(
    val id: String,
    val day: CalendarDay,
    val holidays: List<String>,
    val fastingInformation: FastingInfo
)
