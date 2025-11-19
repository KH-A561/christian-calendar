package ru.akhilko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.day.CalendarDayResourceEntity
import ru.akhilko.core.database.entity.day.CalendarDayResourceFtsEntity
import ru.akhilko.core.database.repository.CalendarDayFtsDao
import ru.akhilko.core.database.util.InstantConverter
import ru.akhilko.core.database.util.StringListConverter

@Database(
    entities = [
        CalendarDayResourceEntity::class,
        CalendarDayResourceFtsEntity::class
    ],
    version = 2
)
@TypeConverters(
    InstantConverter::class,
    StringListConverter::class
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarFtsRepository(): CalendarDayFtsDao
    abstract fun calendarDayDao(): CalendarDayDao
}
