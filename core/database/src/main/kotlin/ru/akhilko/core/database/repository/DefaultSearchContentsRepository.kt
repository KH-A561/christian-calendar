package ru.akhilko.core.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult
import ru.akhilko.christian_calendar.core.data.repository.SearchContentsRepository
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.day.asResource
import javax.inject.Inject

internal class DefaultSearchContentsRepository @Inject constructor(
    private val dayDao: CalendarDayDao,
) : SearchContentsRepository {

    override fun searchContents(searchQuery: String): Flow<SearchResult> =
        dayDao.searchByText(searchQuery.trim().lowercase())
            .map { entities -> SearchResult(dayData = entities.map { it.asResource() }) }

    override fun getSearchContentsCount(): Flow<Int> = dayDao.getCount()
}
