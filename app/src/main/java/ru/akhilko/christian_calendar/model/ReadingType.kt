package ru.akhilko.christian_calendar.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReadingType(val title : String) {
    APOSTLE("Ап.")
}