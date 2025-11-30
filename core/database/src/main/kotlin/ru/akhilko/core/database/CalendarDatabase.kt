package ru.akhilko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.entity.Converters
import ru.akhilko.core.database.entity.day.CalendarDayEntity
import ru.akhilko.core.database.entity.day.CalendarDayFtsEntity
import ru.akhilko.core.database.repository.CalendarDayFtsDao

@Database(
    entities = [
        CalendarDayEntity::class,
        CalendarDayFtsEntity::class
    ],
    version = 2 // ВАЖНО: нужно будет увеличить версию и добавить миграцию
)
@TypeConverters(
    Converters::class
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarFtsRepository(): CalendarDayFtsDao
    abstract fun calendarDayDao(): CalendarDayDao
}
