package ru.akhilko.christian_calendar.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant

data class CalendarDay(
    val id: String,
    val dayOfWeek: DayOfWeek,
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val title: String? = null,
    val lastUpdated: Instant = Clock.System.now(),
    val liturgical: LiturgicalInfo,
    val saintInfos: List<SaintInfo>,
    val readings: List<Reading>,
    val searchText: String? = "",
    val tags: List<String>? = emptyList()
)