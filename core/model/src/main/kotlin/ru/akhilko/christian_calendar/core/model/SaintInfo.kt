package ru.akhilko.christian_calendar.core.model

data class SaintInfo(
    val id: String,
    val name: String,
    val type: SaintCommemorationLevel
)
