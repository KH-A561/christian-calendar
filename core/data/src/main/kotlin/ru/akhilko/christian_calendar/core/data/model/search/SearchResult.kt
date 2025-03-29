package ru.akhilko.christian_calendar.core.data.model.search

import ru.akhilko.christian_calendar.core.data.model.DayResource

data class SearchResult (
    val dayData: List<DayResource>
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