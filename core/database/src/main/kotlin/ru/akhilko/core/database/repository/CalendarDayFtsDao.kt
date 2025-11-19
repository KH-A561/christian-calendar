package ru.akhilko.core.database.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.CalendarDayResourceFtsEntity

@Dao
interface CalendarDayFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dayResources: List<CalendarDayResourceFtsEntity>)

    @Query("SELECT id FROM dayResourcesFts WHERE dayResourcesFts MATCH :query")
    fun searchAll(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM dayResourcesFts")
    fun getCount(): Flow<Int>
}