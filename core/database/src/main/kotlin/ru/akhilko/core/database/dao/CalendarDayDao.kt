package ru.akhilko.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.CalendarDayResourceEntity
import ru.akhilko.core.database.entity.day.PopulatedCalendarDayResource

@Dao
interface CalendarDayDao {
    @Transaction
    @Query("SELECT * FROM calendar_day_resource WHERE id IN (:ids)")
    fun getDaysByIds(ids: List<String>): Flow<List<PopulatedCalendarDayResource>>

    @Transaction
    @Query("SELECT * FROM calendar_day_resource")
    fun getAll(): Flow<List<PopulatedCalendarDayResource>>

    @Transaction
    @Query(
        """
        SELECT * FROM calendar_day_resource
        WHERE gregorianYear = :year AND gregorianMonth = :month
        """
    )
    fun getDaysByMonth(year: Int, month: Int): Flow<List<PopulatedCalendarDayResource>>

    @Upsert
    suspend fun upsert(day: CalendarDayResourceEntity)
}
