package ru.akhilko.core.database.entity.day

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "calendar_days_fts")
@Fts4(contentEntity = CalendarDayEntity::class)
data class CalendarDayFtsEntity(
    @ColumnInfo(name = "search_text")
    val searchText: String
)
