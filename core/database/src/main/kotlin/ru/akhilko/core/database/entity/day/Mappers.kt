package ru.akhilko.core.database.entity.day

import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.database.dto.CalendarDayDto

fun CalendarDayDto.toEntity(): CalendarDayEntity {
    // Create a simple search string by concatenating title and saints' names.
    val searchText = (title + " " + week + " " + saints.joinToString(" ")).trim().lowercase()

    return CalendarDayEntity(
        id = id,
        dayOfWeek = DayOfWeek.valueOf(dayOfWeek.uppercase()),
        gregorianDay = gregorianDay,
        gregorianMonth = gregorianMonth,
        gregorianYear = gregorianYear,
        julianDay = julianDay,
        julianMonth = julianMonth,
        julianYear = julianYear,
        lastUpdated = "", // This field will be updated from Firestore
        title = title,
        week = week,
        dayTypes = dayTypes.map { DayType.findByName(it) },
        liturgicalInfo = LiturgicalInfo(
            importance = liturgicalInfo.importance
        ),
        fastingInfo = FastingInfo(
            fastingLevel = FastingLevel.valueOf(fastingInfo.fastingLevel.uppercase()),
            allowed = fastingInfo.allowed,
            fastingName = fastingInfo.fastingName
        ),
        readings = readings,
        saints = saints,
        searchText = searchText
    )
}
