package ru.akhilko.christian_calendar.core.model

enum class DayType {
    EASTER,
    TWELVE_GREAT_FEASTS, GREAT_FEAST, FEAST,
    LONG_FAST, FAST,
    COMMEMORATION,
    UNKNOWN;

    fun isFeast(): Boolean {
        return this == GREAT_FEAST ||
                this == EASTER ||
                this == FEAST ||
                this == TWELVE_GREAT_FEASTS
    }

    companion object {
        fun findByName(it: String): DayType {
            if (it.isBlank()) {
                return UNKNOWN
            }

            for (dayType in DayType.entries) {
                if (dayType.name == it.uppercase()) {
                    return dayType
                }
            }

            return UNKNOWN
        }
    }
}
