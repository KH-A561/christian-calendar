package ru.akhilko.christian_calendar.core.data.model

import ru.akhilko.christian_calendar.core.model.CalendarDay

fun CalendarDay.toResource(): CalendarDayResource {
    return CalendarDayResource(
        id = this.id,
        day = this,
        holidays = this.saints.flatMap { it.names },
        fastingInformation = this.fastingInfo
    )
}
