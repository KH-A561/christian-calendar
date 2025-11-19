package ru.akhilko.core.database.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.akhilko.core.database.entity.day.PopulatedCalendarDayResource
import javax.inject.Inject

class FirestoreCalendarDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getCalendarDays(): List<PopulatedCalendarDayResource> {
        return firestore.collection("calendar_days")
            .orderBy("gregorianDay")
            .get()
            .await()
            .toObjects(PopulatedCalendarDayResource::class.java)
    }

    suspend fun getCalendarDaysByMonth(year: Int, month: Int): List<PopulatedCalendarDayResource> {
        return firestore.collection("calendar_days")
            .whereEqualTo("gregorianMonth", month)
            .whereEqualTo("gregorianYear", year)
            .orderBy("gregorianDay")
            .get()
            .await()
            .toObjects(PopulatedCalendarDayResource::class.java)
    }
}
