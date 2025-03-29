package ru.akhilko.core.database.dao

import androidx.room.Dao

@Dao
interface DayResourceFtsDao {
    fun getDaysResources(useFilterDaysIds: Boolean = false,
                         filterDaysIds: Set<String> = emptySet(),)
}