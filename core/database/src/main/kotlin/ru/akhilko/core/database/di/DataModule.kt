
package ru.akhilko.core.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import ru.akhilko.core.Dispatcher
import ru.akhilko.core.Dispatchers
import ru.akhilko.core.data.repository.CalendarDayRepository
import ru.akhilko.core.data.repository.SearchContentsRepository
import ru.akhilko.core.database.dao.CalendarDayDao
import ru.akhilko.core.database.repository.CalendarDayFtsDao
import ru.akhilko.core.database.repository.DefaultCalendarDayRepository
import ru.akhilko.core.database.repository.DefaultSearchContentsRepository
import ru.akhilko.core.database.repository.FirestoreCalendarDataSource
import ru.akhilko.core.database.repository.LocalCalendarDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesSearchContentsRepository(
        dayDao: CalendarDayDao,
        dayFtsRepository: CalendarDayFtsDao,
        @Dispatcher(Dispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): SearchContentsRepository = DefaultSearchContentsRepository(dayDao, dayFtsRepository, ioDispatcher)

    @Provides
    @Singleton
    fun providesCalendarDayRepository(
        calendarDayDao: CalendarDayDao,
        firestoreDataSource: FirestoreCalendarDataSource,
        localCalendarDataSource: LocalCalendarDataSource,
        searchContentsRepository: SearchContentsRepository
    ): CalendarDayRepository = DefaultCalendarDayRepository(
        calendarDayDao,
        firestoreDataSource,
        localCalendarDataSource,
        searchContentsRepository
    )

    @Provides
    @Singleton
    fun provideLocalCalendarDataSource(
        @ApplicationContext context: Context,
        json: Json
    ): LocalCalendarDataSource = LocalCalendarDataSource(context, json)
    
    @Provides
    @Dispatcher(Dispatchers.IO)
    fun providesIoDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
}
