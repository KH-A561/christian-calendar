package ru.akhilko.christian_calendar.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult

interface SearchContentsRepository {
    fun searchContents(searchQuery: String): Flow<SearchResult>
    fun getSearchContentsCount(): Flow<Int>
}
