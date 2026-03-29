package ru.akhilko.christian_calendar.core.data.model

import android.content.Context
import ru.akhilko.data.R

enum class DayType(private val resourceId: Int) {
    EASTER(R.string.core_data_day_type_easter),
    GREAT_FEAST(R.string.core_data_day_type_great_feast),
    TWELVE_GREAT_FEASTS(R.string.core_data_day_type_twelve_great_feasts),
    FEAST(R.string.core_data_day_type_feast),
    LONG_FAST(R.string.core_data_day_type_long_fast),
    FAST(R.string.core_data_day_type_fast),
    COMMEMORATION(R.string.core_data_day_type_commemoration),
    UNKNOWN(R.string.core_data_day_type_unknown);

    fun isFeast(): Boolean {
        return this == GREAT_FEAST ||
                this == EASTER ||
                this == FEAST ||
                this == TWELVE_GREAT_FEASTS
    }

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