package ru.akhilko.core.domain

import kotlinx.coroutines.flow.Flow
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult
import ru.akhilko.core.database.repository.SearchContentsRepository
import javax.inject.Inject

class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
) {
    operator fun invoke(
        searchQuery: String,
    ): Flow<SearchResult> = searchContentsRepository.searchContents(searchQuery)
}