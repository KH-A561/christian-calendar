package ru.akhilko.core.database.entity.day

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "dayResourcesFts")
@Fts4
data class CalendarDayResourceFtsEntity (
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "weekDay")
    val weekDay: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "weekInfo")
    val weekInfo: String,

    @ColumnInfo(name = "primarySaints")
    val primarySaints: String,

    @ColumnInfo(name = "secondarySaints")
    val secondarySaints: String?,

    @ColumnInfo(name = "readings")
    val readings: String?,

    @ColumnInfo(name = "tags")
    val tags: String?,

    @ColumnInfo(name = "fasting")
    val fasting: String?
)