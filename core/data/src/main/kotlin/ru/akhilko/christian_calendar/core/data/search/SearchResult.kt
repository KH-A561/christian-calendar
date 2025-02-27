package ru.akhilko.christian_calendar.core.data.search

import ru.akhilko.christian_calendar.core.data.model.DayResource

data class SearchResult(
    val dayData: List<DayResource>,
    val searchHits: List<String>
)