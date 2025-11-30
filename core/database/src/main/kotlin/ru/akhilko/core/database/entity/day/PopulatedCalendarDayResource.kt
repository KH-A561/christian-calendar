package ru.akhilko.core.database.entity.day

import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType

fun CalendarDayEntity.asResource(): CalendarDayResource {
    val day = this.asModel()
    return CalendarDayResource(
        id = this.id,
        day = day,
        holidays = if (day.liturgicalInfo.dayType == DayType.FEAST || day.liturgicalInfo.dayType == DayType.MEMORIAL) listOf(day.title) else emptyList(),
        fastingInformation = day.fastingInfo
    )
}

fun CalendarDayEntity.asFtsEntity(): CalendarDayFtsEntity {
    return CalendarDayFtsEntity(
        searchText = this.searchText
    )
}

fun CalendarDayEntity.asModel() = CalendarDay(
    dayOfWeek = this.dayOfWeek,
    gregorianDay = this.gregorianDay,
    gregorianMonth = this.gregorianMonth,
    gregorianYear = this.gregorianYear,
    lastUpdated = this.lastUpdated,
    title = this.title,
    week = this.week,
    liturgicalInfo = this.liturgicalInfo,
    fastingInfo = this.fastingInfo,
    readings = this.readings,
    saints = this.saints,
    searchText = this.searchText
)
