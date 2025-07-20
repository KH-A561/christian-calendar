package ru.akhilko.core.database.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.akhilko.core.database.entity.day.DayResourceFtsEntity

@Dao
interface CalendarFtsRepository {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dayResources: List<DayResourceFtsEntity>)

    @Query("SELECT id FROM dayResourcesFts WHERE dayResourcesFts MATCH :query")
    fun searchAll(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM dayResourcesFts")
    fun getCount(): Flow<Int>
}