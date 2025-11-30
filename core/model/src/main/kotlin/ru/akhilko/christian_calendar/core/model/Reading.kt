package ru.akhilko.christian_calendar.core.model

data class Reading(
    val type: ReadingType,
    val passage: String,
    val description: String
)