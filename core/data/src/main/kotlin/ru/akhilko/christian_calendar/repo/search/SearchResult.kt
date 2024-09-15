package ru.akhilko.christian_calendar.repo.search

import ru.akhilko.christian_calendar.core.model.data.DayData

data class SearchResult(
    val dayData: DayData,
    val searchHits: List<String>
)