package ru.akhilko.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.CalendarDayEntity

@Dao
interface CalendarDayDao {
    @Transaction
    @Query("SELECT * FROM calendar_days WHERE id IN (:ids)")
    fun getDaysByIds(ids: List<String>): Flow<List<CalendarDayEntity>>

    @Transaction
    @Query("SELECT * FROM calendar_days")
    fun getAll(): Flow<List<CalendarDayEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM calendar_days
        WHERE gregorian_year = :year AND gregorian_month = :month
        """
    )
    fun getDaysByMonth(year: Int, month: Int): Flow<List<CalendarDayEntity>>

    @Upsert
    suspend fun upsert(day: CalendarDayEntity)

    @Upsert
    suspend fun upsertAll(days: List<CalendarDayEntity>)
}
