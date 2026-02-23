package ru.akhilko.christian_calendar.core.model

data class FastingInfo(
    val fastingLevel: FastingLevel = FastingLevel.NONE,
    val allowed: List<String> = emptyList(),
    val fastingName: String? = null // Добавляем поле для названия поста
)
