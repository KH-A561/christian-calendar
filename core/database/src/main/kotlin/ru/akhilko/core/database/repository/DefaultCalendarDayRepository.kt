package ru.akhilko.core.database.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.common.DateConverter
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.data.repository.AuthRepository
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.day.CalendarDayEntity
import ru.akhilko.core.database.entity.day.asResource
import ru.akhilko.core.database.entity.day.toEntity
import javax.inject.Inject

internal class DefaultCalendarDayRepository @Inject constructor(
    private val calendarDayDao: CalendarDayDao,
    private val firestoreDataSource: FirestoreCalendarDataSource,
    private val localDataSource: LocalCalendarDataSource,
    private val authRepository: AuthRepository,
) : CalendarDayRepository {

    override fun getDay(id: String): Flow<CalendarDayResource?> {
        return calendarDayDao.getDayById(id).map { it?.asResource() }
    }

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

    override suspend fun sync(year: Int) {
        authRepository.signInAnonymouslyIfNeeded()

        try {
            val currentDays = calendarDayDao.getAll().first()
            if (currentDays.isEmpty()) {
                val localData = localDataSource.getCalendarData().map { it.toEntity() }
                calendarDayDao.upsertAll(localData)
            }
        } catch (e: Exception) {
            Log.e("Sync", "Failed to load local data", e)
        }

        try {
            val remoteData = firestoreDataSource.getYearData(year)
            if (remoteData.isNotEmpty()) {
                calendarDayDao.upsertAll(remoteData.map { (id, firestoreDay) ->
                    firestoreDay.toEntity(id)
                })
            }
        } catch (e: Exception) {
            Log.w("Sync", "Firestore sync FAILED for year $year", e)
        }

    }
}

private fun FirestoreDay.toEntity(id: String): CalendarDayEntity {
    val liturgicalInfo = LiturgicalInfo(
        importance = this.liturgical.importance
    )

    val fastingInfo = FastingInfo(
        fastingLevel = if (this.fastingInfo.fastingLevel.isBlank()) FastingLevel.NONE else FastingLevel.valueOf(this.fastingInfo.fastingLevel.uppercase()),
        allowed = this.fastingInfo.allowed,
        fastingName = this.fastingInfo.fastingName
    )

    val (julianYear, julianMonth, julianDay) = DateConverter.gregorianToJulian(
        this.gregorianYear,
        this.gregorianMonth,
        this.gregorianDay
    )

    return CalendarDayEntity(
        id = id,
        dayOfWeek = DayOfWeek.valueOf(this.dayOfWeek.uppercase()),
        gregorianDay = this.gregorianDay,
        gregorianMonth = this.gregorianMonth,
        gregorianYear = this.gregorianYear,
        julianDay = julianDay,
        julianMonth = julianMonth,
        julianYear = julianYear,
        lastUpdated = this.lastUpdated.toString(),
        title = this.title,
        week = this.week,
        dayTypes = this.dayTypes.map { DayType.findByName((it)) },
        liturgicalInfo = liturgicalInfo,
        fastingInfo = fastingInfo,
        readings = this.readings,
        saints = this.saints,
        searchText = "${this.title} ${this.week} ${this.saints.joinToString(" ")}".trim().lowercase()
    )
}
