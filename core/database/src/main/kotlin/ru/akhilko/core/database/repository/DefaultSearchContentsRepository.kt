/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.akhilko.core.database.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult
import ru.akhilko.core.Dispatcher
import ru.akhilko.core.Dispatchers
import ru.akhilko.core.database.entity.day.PopulatedDayResource
import java.util.stream.Collectors
import javax.inject.Inject

internal class DefaultSearchContentsRepository @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val calendarFtsRepository: CalendarFtsRepository,
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    override suspend fun populateFtsData() {
        withContext(ioDispatcher) {
            calendarFtsRepository.insertAll(
                calendarRepository.getDaysResources(
                    useFilterDaysIds = false
                )
                    .first()
                    .map(PopulatedDayResource::asFtsEntity),
            )
        }
    }

    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // Surround the query by asterisks to match the query when it's in the middle of
        // a word
        val daysResourceIds = calendarFtsRepository.searchAll("*$searchQuery*")

        val daysResourcesFlow = daysResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                calendarRepository.getDaysResources(useFilterDaysIds = true, filterDaysIds = it)
            }

        return daysResourcesFlow.map { r ->
            SearchResult(
                dayData = r.stream().map { pr -> pr.asModel() }.collect(Collectors.toList())
            )
        }
    }

    override fun getSearchContentsCount(): Flow<Int> =
        calendarFtsRepository.getCount()
}
