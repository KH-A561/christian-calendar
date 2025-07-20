package ru.akhilko.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.akhilko.core.database.CalendarDatabase
import ru.akhilko.core.database.repository.CalendarFtsRepository

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesDayResourceFtsDao(
        database: CalendarDatabase,
    ): CalendarFtsRepository = database.calendarFtsRepository()
}
