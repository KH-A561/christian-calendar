package ru.akhilko.christian_calendar.core.data.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import ru.akhilko.christian_calendar.core.model.data.Day
import java.util.UUID

data class DayResource (
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
) : Resource {
    override fun getSearchableTextFieldNames() : List<String> {
        return listOf(::title.name, ::weekInfo.name, ::primarySaints.name, ::secondarySaints.name,
            ::readings.name, ::tags.name, ::fasting.name)
    }

    fun day() : Day {
        return Day(id, weekDay, oldStyleDate, newStyleDate, title, weekInfo, primarySaints,
            secondarySaints, readings, tags, fasting)
    }
}