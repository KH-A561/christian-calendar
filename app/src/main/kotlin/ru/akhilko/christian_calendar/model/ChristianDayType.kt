package ru.akhilko.christian_calendar.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChristianDayType(val title: String) {
    FEAST("Праздник"), FAST("Пост");


}