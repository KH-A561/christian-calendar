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

    @Query("DELETE FROM calendar_days_fts")
    fun clearAll()

    /**
     * Выполняет полную пересборку поискового индекса на стороне SQLite.
     * Это гораздо эффективнее, чем ручная вставка из Kotlin-кода.
     */
    @Query("INSERT INTO calendar_days_fts(calendar_days_fts) VALUES('rebuild')")
    fun rebuildFtsIndex()
}
