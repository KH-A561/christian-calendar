package ru.akhilko.core.ui.format

import java.time.DayOfWeek
import java.time.LocalDate

// На JVM kotlinx.datetime.DayOfWeek — typealias для java.time.DayOfWeek,
// поэтому одной перегрузки достаточно для обоих типов.
fun weekdayRu(dayOfWeek: DayOfWeek, short: Boolean = false): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> if (short) "Пн" else "Понедельник"
    DayOfWeek.TUESDAY -> if (short) "Вт" else "Вторник"
    DayOfWeek.WEDNESDAY -> if (short) "Ср" else "Среда"
    DayOfWeek.THURSDAY -> if (short) "Чт" else "Четверг"
    DayOfWeek.FRIDAY -> if (short) "Пт" else "Пятница"
    DayOfWeek.SATURDAY -> if (short) "Сб" else "Суббота"
    DayOfWeek.SUNDAY -> if (short) "Вс" else "Воскресенье"
}

fun monthGenitiveRu(month: Int): String = when (month) {
    1 -> "января"
    2 -> "февраля"
    3 -> "марта"
    4 -> "апреля"
    5 -> "мая"
    6 -> "июня"
    7 -> "июля"
    8 -> "августа"
    9 -> "сентября"
    10 -> "октября"
    11 -> "ноября"
    12 -> "декабря"
    else -> ""
}

fun monthNominativeRu(month: Int): String = when (month) {
    1 -> "Январь"
    2 -> "Февраль"
    3 -> "Март"
    4 -> "Апрель"
    5 -> "Май"
    6 -> "Июнь"
    7 -> "Июль"
    8 -> "Август"
    9 -> "Сентябрь"
    10 -> "Октябрь"
    11 -> "Ноябрь"
    12 -> "Декабрь"
    else -> ""
}

fun monthYearRu(month: Int, year: Int): String = "${monthNominativeRu(month)} $year"

fun formatGregorianRu(date: LocalDate): String =
    "${date.dayOfMonth} ${monthGenitiveRu(date.monthValue)} ${date.year}"
