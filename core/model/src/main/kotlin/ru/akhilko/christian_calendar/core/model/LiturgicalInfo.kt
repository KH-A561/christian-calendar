package ru.akhilko.christian_calendar.core.model

data class LiturgicalInfo (
    val weekInfo: String,
    val dayType: DayType,
    val color: LiturgicalColor
) {

}