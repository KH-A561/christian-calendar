
package ru.akhilko.core.database.entity.day

import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.christian_calendar.core.model.Reading
import ru.akhilko.christian_calendar.core.model.ReadingType
import ru.akhilko.christian_calendar.core.model.SaintInfo
import ru.akhilko.core.database.dto.CalendarDayDto

fun CalendarDayDto.toEntity(): CalendarDayEntity {
    // Create a simple search string by concatenating title and saints' names.
    val searchText = title + " " + saints.joinToString(" ")

    return CalendarDayEntity(
        id = id,
        dayOfWeek = DayOfWeek.valueOf(dayOfWeek.uppercase()),
        gregorianDay = gregorianDay,
        gregorianMonth = gregorianMonth,
        gregorianYear = gregorianYear,
        lastUpdated = "", // This field will be updated from Firestore
        title = title,
        week = week,
        liturgicalInfo = LiturgicalInfo(
            color = LiturgicalColor.valueOf(liturgicalInfo.color.uppercase()),
            dayType = DayType.valueOf(liturgicalInfo.dayType.uppercase()),
            importance = liturgicalInfo.importance
        ),
        fastingInfo = FastingInfo(
            fastingLevel = FastingLevel.valueOf(fastingInfo.fastingLevel.uppercase()),
            allowed = fastingInfo.allowed
        ),
        readings = readings.map { readingString ->
            Reading(
                type = ReadingType.UNKNOWN, // Keep it simple for now
                passage = "",
                description = readingString
            )
        },
        saints = listOf(SaintInfo(names = saints)),
        searchText = searchText
    )
}

