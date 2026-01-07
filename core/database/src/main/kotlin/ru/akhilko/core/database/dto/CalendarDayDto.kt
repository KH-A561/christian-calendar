
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
    val fastingInfo: FastingInfoDto,
    val liturgicalInfo: LiturgicalInfoDto,
    val saints: List<String>,
    val readings: List<String>
)

@Serializable
data class FastingInfoDto(
    val fastingLevel: String,
    val allowed: List<String>
)

@Serializable
data class LiturgicalInfoDto(
    val color: String,
    val dayType: String,
    val importance: Int
)
