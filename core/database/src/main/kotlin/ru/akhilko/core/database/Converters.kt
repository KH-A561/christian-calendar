package ru.akhilko.core.database

import androidx.room.TypeConverter
import ru.akhilko.christian_calendar.core.model.DayType

class DayTypesConverter {
    @TypeConverter
    fun fromDayTypes(dayTypes: List<DayType>): String {
        return dayTypes.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayTypes(data: String): List<DayType> {
        if (data.isEmpty()) {
            return emptyList()
        }
        return data.split(",").map { DayType.valueOf(it) }
    }
}
