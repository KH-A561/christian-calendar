package ru.akhilko.christian_calendar.core.model

data class FastingInfo(
    val fastingLevel: FastingLevel,
    val allowedFood: List<String>
) {
}