package ru.akhilko.core.database.entity.day

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource

@Entity(
    tableName = "calendar_days",
)
data class CalendarDayResourceEntity(
    @PrimaryKey
    val id: String,
    val weekDay: String,
    val oldStyleDate: Instant,
    val newStyleDate: Instant,
    val title: String,
    val weekInfo: String,
    val primarySaints: List<String>,
    val secondarySaints: List<String>,
    val readings: Map<String, List<String>>?,
    val tags: List<String>?,
    val fasting: String?
)

fun CalendarDayResourceEntity.asExternalModel() = CalendarDayResource(
    id = id,
    weekDay = DayOfWeek.valueOf(weekDay),
    oldStyleDate = oldStyleDate,
    newStyleDate = newStyleDate,
    title = title,
    weekInfo = weekInfo,
    primarySaints = primarySaints,
    secondarySaints = secondarySaints,
    readings = readings,
    tags = tags,
    fasting = fasting,
)
