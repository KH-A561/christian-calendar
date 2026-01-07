package ru.akhilko.christian_calendar.core.model

import java.time.LocalDate

fun CalendarDay.getGregorianLocalDate(): LocalDate {
    return LocalDate.of(gregorianYear, gregorianMonth, gregorianDay)
}
