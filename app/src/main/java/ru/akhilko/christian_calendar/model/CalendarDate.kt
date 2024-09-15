package ru.akhilko.christian_calendar.model

import kotlinx.serialization.Serializable
import org.threeten.extra.chrono.JulianDate
import ru.akhilko.christian_calendar.model.serializer.DateSerializer
import ru.akhilko.christian_calendar.model.serializer.DayOfWeekSerializer
import ru.akhilko.christian_calendar.model.serializer.JulianDateSerializer
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class CalendarDate(
    @Serializable(with = DateSerializer::class)
    val gregorianDate: LocalDate,
    @Serializable(with = JulianDateSerializer::class)
    val julianDate: JulianDate,
    @Serializable(with = DayOfWeekSerializer::class)
    val dayOfWeek: DayOfWeek
)

