package ru.akhilko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.akhilko.core.database.entity.day.DayResourceFtsEntity
import ru.akhilko.core.database.repository.CalendarFtsRepository
import ru.akhilko.core.database.util.InstantConverter

@Database(
    entities = [
        DayResourceFtsEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarFtsRepository(): CalendarFtsRepository
}
