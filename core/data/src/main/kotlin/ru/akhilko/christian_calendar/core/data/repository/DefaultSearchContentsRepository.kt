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

package ru.akhilko.christian_calendar.core.data.repository

import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.database.model.asFtsEntity
import com.google.samples.apps.nowinandroid.core.model.data.SearchResult
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import ru.akhilko.christian_calendar.core.data.model.DayResource
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult
import ru.akhilko.core.Dispatcher
import ru.akhilko.core.Dispatchers
import ru.akhilko.core.database.dao.DayResourceDao
import ru.akhilko.core.database.dao.DayResourceFtsDao
import javax.inject.Inject

internal class DefaultSearchContentsRepository @Inject constructor(
    private val dayResourceDao: DayResourceDao,
    private val dayResourceFtsDao: DayResourceFtsDao,
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    override suspend fun populateFtsData() {
        withContext(ioDispatcher) {
            dayResourceFtsDao.insertAll(
                dayResourceDao.getDaysResources(
                    useFilterTopicIds = false,
                    useFilterNewsIds = false,
                )
                    .first()
                    .map(PopulatedNewsResource::asFtsEntity),
            )
        }
    }

    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // Surround the query by asterisks to match the query when it's in the middle of
        // a word
        val newsResourceIds = dayResourceFtsDao.searchAllNewsResources("*$searchQuery*")

        val newsResourcesFlow = newsResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                newsResourceDao.getNewsResources(useFilterNewsIds = true, filterNewsIds = it)
            }
        return { newsResources ->
            SearchResult(
                newsResources = newsResources.map { it.asExternalModel() },
            )
        }
    }

    override fun getSearchContentsCount(): Flow<Int> =
        dayResourceFtsDao.getCount()
}
