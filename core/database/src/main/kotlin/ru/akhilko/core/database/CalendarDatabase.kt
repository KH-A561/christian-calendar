package ru.akhilko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.akhilko.core.database.dao.DayResourceDao
import ru.akhilko.core.database.model.DayResourceEntity
import ru.akhilko.core.database.model.DayResourceFtsEntity
import ru.akhilko.core.database.util.InstantConverter

@Database(
    entities = [
        DayResourceEntity::class,
        DayResourceFtsEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun dayResourceDao(): DayResourceDao
}
