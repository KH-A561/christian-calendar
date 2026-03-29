
package ru.akhilko.core.database.dto

import kotlinx.serialization.Serializable

@Serializable
data class CalendarDayDto(
    val id: String,
    val title: String,
    val dayOfWeek: String,
    val gregorianYear: Int,
    val gregorianMonth: Int,
    val gregorianDay: Int,
    val julianYear: Int,
    val julianMonth: Int,
    val julianDay: Int,
    val week: String,
    val dayTypes: List<String> = emptyList(),
    val fastingInfo: FastingInfoDto,
    val liturgicalInfo: LiturgicalInfoDto,
    val saints: List<String>,
    val readings: List<String>
)

@Serializable
data class FastingInfoDto(
    val fastingLevel: String,
    val allowed: List<String>,
    val fastingName: String? = null
)

@Serializable
data class LiturgicalInfoDto(
    val importance: Int
)
