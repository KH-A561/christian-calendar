package ru.akhilko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 4
)
@TypeConverters(
    Converters::class,
    DayTypesConverter::class
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarFtsRepository(): CalendarDayFtsDao
    abstract fun calendarDayDao(): CalendarDayDao
}

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE calendar_days ADD COLUMN julian_day INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE calendar_days ADD COLUMN julian_month INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE calendar_days ADD COLUMN julian_year INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Schema remains the same as fastingName and dayTypes are stored within JSON/Converters
    }
}
