package ru.akhilko.christian_calendar.core.data.model

import android.content.Context
import ru.akhilko.data.R

enum class DayType(private val resourceId: Int) {
    FEAST(R.string.core_data_day_type_feast),
    FAST(R.string.core_data_day_type_fast),
    UNKNOWN(R.string.core_data_day_type_unknown);

    fun getLabel(context: Context): String {
        return context.getString(resourceId)
    }

    fun getResourceId(): Int {
        return resourceId
    }
}

fun findByName(name: String): DayType {
    if (name.isBlank()) {
        return DayType.UNKNOWN
    }

    for (dayType in DayType.entries) {
        if (dayType.name == name.uppercase()) {
            return dayType
        }
    }

    return DayType.UNKNOWN
}