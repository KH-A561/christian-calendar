package ru.akhilko.christian_calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class Day(
    val calendarDate: CalendarDate,
    val title: String,
    val types: List<ChristianDayType>,
    val details: List<String>,
    val readings: Map<ReadingType, List<String>>
)