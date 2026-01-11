
package ru.akhilko.core.database.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.data.repository.SearchContentsRepository
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.day.asResource
import ru.akhilko.core.database.entity.day.toEntity
import javax.inject.Inject

internal class DefaultCalendarDayRepository @Inject constructor(
    private val calendarDayDao: CalendarDayDao,
    private val firestoreDataSource: FirestoreCalendarDataSource,
    private val localDataSource: LocalCalendarDataSource,
    private val searchContentsRepository: SearchContentsRepository
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
        Log.d("Sync", "Starting sync process...")

        // 1. Сначала проверяем, пуста ли база. Если пуста - ОБЯЗАТЕЛЬНО грузим локальные данные.
        try {
            val currentDays = calendarDayDao.getAll().first()
            if (currentDays.isEmpty()) {
                Log.d("Sync", "Database is empty. Loading local data from assets...")
                val localData = localDataSource.getCalendarData().map { it.toEntity() }
                calendarDayDao.upsertAll(localData)
                Log.d("Sync", "Local data loaded successfully.")
            }
        } catch (e: Exception) {
            Log.e("Sync", "Failed to load local data", e)
        }

        // 2. Попытка обновиться из Firestore (только если есть интернет, или просто оборачиваем в try-catch)
        try {
            Log.d("Sync", "Attempting to fetch data from Firestore...")
            val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            val remoteData = firestoreDataSource.getYearData(currentYear)
            // TODO: Когда будет готов маппер для Firestore моделей, раскомментировать:
            /*
            if (remoteData.isNotEmpty()) {
                calendarDayDao.upsertAll(remoteData.map { it.toEntity() })
                Log.d("Sync", "Remote data updated from Firestore.")
            }
            */
        } catch (e: Exception) {
            Log.w("Sync", "Firestore sync failed (possibly no internet)", e)
        }

        // 3. Обновляем FTS (поиск) в любом случае
        try {
            Log.d("Sync", "Populating FTS data...")
            searchContentsRepository.populateFtsData()
            Log.d("Sync", "FTS data populated.")
        } catch (e: Exception) {
            Log.e("Sync", "FTS population failed", e)
        }
    }
}
