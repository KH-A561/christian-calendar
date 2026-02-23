package ru.akhilko.core.database.entity.day

import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.model.CalendarDay

fun CalendarDayEntity.asResource(): CalendarDayResource {
    return CalendarDayResource(
        id = this.id,
        day = this.asModel()
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
    julianDay = this.julianDay,
    julianMonth = this.julianMonth,
    julianYear = this.julianYear,
    lastUpdated = this.lastUpdated,
    title = this.title,
    week = this.week,
    dayTypes = this.dayTypes,
    liturgicalInfo = this.liturgicalInfo,
    fastingInfo = this.fastingInfo,
    readings = this.readings,
    saints = this.saints,
    searchText = this.searchText
)
