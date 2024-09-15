package ru.akhilko.core.calendarsystem

import androidx.compose.runtime.Immutable
import java.io.Serializable
import java.time.YearMonth

/**
 * Represents a month on the calendar.
 *
 * @param yearMonth the calendar month value.
 * @param weeks the weeks in this month.
 */
@Immutable
data class CalendarMonth(val yearMonth: YearMonth, val weeks: List<CalendarWeek>) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalendarMonth

        if (yearMonth != other.yearMonth) return false
        if (weeks.first() != other.weeks.first()) return false
        if (weeks.last() != other.weeks.last()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearMonth.hashCode()
        result = 31 * result + weeks.first().hashCode()
        result = 31 * result + weeks.last().hashCode()
        return result
    }

    override fun toString(): String {
        return "CalendarMonth { " +
                "first = ${weeks.first()}, " +
                "last = ${weeks.last()} " +
                "} "
    }
}