package ru.akhilko.christian_calendar.core.common

import java.util.GregorianCalendar
import java.util.Calendar

object DateConverter {

    fun gregorianToJulian(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val cal = GregorianCalendar(year, month - 1, day)
        val julianCal = GregorianCalendar()
        julianCal.time = cal.time
        val diff = (julianCal.get(Calendar.ERA) * 2 - 1) * (13) // Разница в 13 дней для 20-21 веков
        julianCal.add(Calendar.DATE, -diff)

        return Triple(
            julianCal.get(Calendar.YEAR),
            julianCal.get(Calendar.MONTH) + 1,
            julianCal.get(Calendar.DAY_OF_MONTH)
        )
    }
}