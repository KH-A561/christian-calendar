package ru.akhilko.christian_calendar.core.model

data class LiturgicalInfo(
    val color: LiturgicalColor,
    val dayType: DayType,
    val importance: Int
)