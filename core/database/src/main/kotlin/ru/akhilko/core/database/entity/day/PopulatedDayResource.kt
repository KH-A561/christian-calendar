package ru.akhilko.core.database.entity.day

import androidx.room.Embedded
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.data.model.DayResource

data class PopulatedDayResource(
    @Embedded
    val entity: DayResourceEntity
) {
    fun asModel(): DayResource {
        return DayResource(
            id = entity.id,
            weekDay = DayOfWeek.valueOf(entity.weekDay),
            oldStyleDate = entity.oldStyleDate,
            newStyleDate = entity.newStyleDate,
            title = entity.title,
            weekInfo = entity.weekInfo,
            primarySaints = entity.primarySaints,
            secondarySaints = entity.secondarySaints,
            readings = entity.readings,
            tags = entity.tags,
            fasting = entity.fasting
        )
    }

    fun asFtsEntity(): DayResourceFtsEntity {
        return DayResourceFtsEntity(
            id = entity.id,
            weekDay = entity.weekDay,
            title = entity.title,
            weekInfo = entity.weekInfo,
            primarySaints = entity.primarySaints.joinToString(),
            secondarySaints = entity.secondarySaints.joinToString(),
            readings = entity.readings?.entries?.stream()
                ?.map { e -> e.key.plus(": ").plus(e.value.joinToString()) }
                ?.reduce { e1, e2 -> listOf(e1, e2).joinToString("\n") }
                ?.orElse(""),
            tags = entity.tags?.joinToString(),
            fasting = entity.fasting
        )

    }
}