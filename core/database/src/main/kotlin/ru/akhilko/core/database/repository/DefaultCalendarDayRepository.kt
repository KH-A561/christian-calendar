package ru.akhilko.core.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.day.asResource
import javax.inject.Inject

internal class DefaultCalendarDayRepository @Inject constructor(
    private val calendarDayDao: CalendarDayDao,
    private val firestoreDataSource: FirestoreCalendarDataSource
) : CalendarDayRepository {

    override fun getDaysByIds(ids: List<String>): Flow<List<CalendarDayResource>> {
        return calendarDayDao.getDaysByIds(ids).map { populated ->
            populated.map { it.asResource() }
        }
    }

    override fun getAll(): Flow<List<CalendarDayResource>> {
        return calendarDayDao.getAll().map { populated ->
            populated.map { it.asResource() }
        }
    }

    override fun getDaysByMonth(
        year: Int,
        month: Int
    ): Flow<List<CalendarDayResource>> {
        return calendarDayDao.getDaysByMonth(year, month).map { populated ->
            populated.map { it.asResource() }
        }
    }

    override suspend fun sync() {
        val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        val remoteData = firestoreDataSource.getYearData(currentYear)
        calendarDayDao.upsertAll(remoteData)
    }
}
