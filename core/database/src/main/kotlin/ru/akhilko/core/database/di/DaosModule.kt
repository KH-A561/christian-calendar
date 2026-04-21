package ru.akhilko.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.akhilko.core.database.CalendarDatabase
import ru.akhilko.core.database.dao.CalendarDayDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesCalendarDayDao(
        database: CalendarDatabase,
    ): CalendarDayDao = database.calendarDayDao()
}
