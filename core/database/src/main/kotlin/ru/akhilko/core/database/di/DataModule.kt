package ru.akhilko.core.database.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import ru.akhilko.christian_calendar.core.data.repository.AuthRepository
import ru.akhilko.christian_calendar.core.data.repository.CalendarDayRepository
import ru.akhilko.christian_calendar.core.data.repository.SearchContentsRepository
import ru.akhilko.core.database.dao.CalendarDayDao
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
    ): SearchContentsRepository = DefaultSearchContentsRepository(dayDao)

    @Provides
    @Singleton
    fun providesCalendarDayRepository(
        calendarDayDao: CalendarDayDao,
        firestoreDataSource: FirestoreCalendarDataSource,
        localCalendarDataSource: LocalCalendarDataSource,
        authRepository: AuthRepository,
    ): CalendarDayRepository = DefaultCalendarDayRepository(
        calendarDayDao,
        firestoreDataSource,
        localCalendarDataSource,
        authRepository,
    )

    @Provides
    @Singleton
    fun provideLocalCalendarDataSource(
        @ApplicationContext context: Context,
        json: Json
    ): LocalCalendarDataSource = LocalCalendarDataSource(context, json)

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}
