package ru.akhilko.christian_calendar.core.model.data

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import java.util.UUID

data class Day(
    val id: String = UUID.randomUUID().toString(),
    val weekDay: DayOfWeek,
    val oldStyleDate: Instant,
    val newStyleDate: Instant,
    val title: String?,
    val weekInfo: String?,
    val primarySaints: List<String> = ArrayList(),
    val secondarySaints: List<String>?,
    val readings: Map<String, List<String>>?,
    val tags: List<String>?,
    val fasting: String?
)