package ru.akhilko.christian_calendar.core.data.model.search

import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource

data class SearchResult (
    val dayData: List<CalendarDayResource>
) {
    fun isEmpty() : Boolean {
        return dayData.isEmpty()
    }

    fun isNotEmpty() : Boolean {
        return !isEmpty()
    }

    fun size() : Int {
        return dayData.size
    }
}