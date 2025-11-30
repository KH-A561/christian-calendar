package ru.akhilko.core.database.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.CalendarDayFtsEntity

@Dao
interface CalendarDayFtsDao {
    @Query("SELECT rowid FROM calendar_days_fts WHERE calendar_days_fts MATCH :query")
    fun searchAll(query: String): Flow<List<Int>>

    @Query("SELECT count(*) FROM calendar_days_fts")
    fun getCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(days: List<CalendarDayFtsEntity>)
}
