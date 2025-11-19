package ru.akhilko.christian_calendar.core.data.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.christian_calendar.core.model.Reading
import ru.akhilko.christian_calendar.core.model.SaintCommemorationLevel
import ru.akhilko.christian_calendar.core.model.SaintInfo
import java.util.UUID

data class CalendarDayResource(
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
    override fun getSearchableTextFieldNames(): List<String> {
        return listOf(
            ::title.name, ::weekInfo.name, ::primarySaints.name, ::secondarySaints.name,
            ::readings.name, ::tags.name, ::fasting.name
        )
    }

    fun day(): CalendarDay {
        val dateTime = newStyleDate.toLocalDateTime(TimeZone.UTC)
        val saints = primarySaints.map {
            SaintInfo(
                id = UUID.randomUUID().toString(),
                name = it,
                type = SaintCommemorationLevel.MEMORIAL
            )
        } + (secondarySaints?.map {
            SaintInfo(
                id = UUID.randomUUID().toString(),
                name = it,
                type = SaintCommemorationLevel.OPTIONAL_MEMORIAL
            )
        } ?: emptyList())

        val readingList = readings?.flatMap { (type, refs) ->
            refs.map { ref -> Reading(name = type, reference = ref) }
        } ?: emptyList()

        return CalendarDay(
            id = id,
            dayOfWeek = weekDay,
            gregorianDay = dateTime.dayOfMonth,
            gregorianMonth = dateTime.monthNumber,
            gregorianYear = dateTime.year,
            title = title,
            lastUpdated = newStyleDate,
            liturgical = LiturgicalInfo(
                weekInfo = weekInfo ?: "",
                //TODO: Implement logic for DayType
                dayType = if (fasting != null) DayType.FAST else DayType.ORDINARY,
                //TODO: Implement logic for LiturgicalColor
                color = LiturgicalColor.GREEN
            ),
            saintInfos = saints,
            readings = readingList,
            tags = tags ?: emptyList(),
            searchText = listOfNotNull(title, weekInfo, fasting)
                .plus(primarySaints)
                .plus(secondarySaints ?: emptyList())
                .plus(readings?.values?.flatten() ?: emptyList())
                .joinToString(separator = " ")
        )
    }
}
