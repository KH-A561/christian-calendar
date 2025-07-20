package ru.akhilko.core.database.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.akhilko.core.database.entity.day.PopulatedDayResource
import ru.akhilko.core.database.entity.month.MonthSummaryEntity

class CalendarFirestoreRepository : CalendarRepository {
    companion object {
        private const val DAYS = "days"
        private const val ID = "id"
        private const val MONTH = "month"
        private const val YEAR = "year"
    }

    private val firestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(persistentCacheSettings {})
            .build()
    }

    override fun getDaysResources(
        useFilterDaysIds: Boolean,
        filterDaysIds: Set<String>
    ): Flow<List<PopulatedDayResource>> {
        val collection = firestore.collection(DAYS)
        if (useFilterDaysIds) {
            collection.whereIn(ID, filterDaysIds.toList())
        }
        return collection.snapshots()
            .map { snap -> snap.toObjects(PopulatedDayResource::class.java) }
    }

    override fun getMonthsInfo(
        useFilterMonths: Boolean,
        filterMonths: Set<Int>,
        filterYear: Int
    ): Flow<List<MonthSummaryEntity>> {
        val collection = firestore.collection(DAYS)
        collection.whereEqualTo(YEAR, filterYear)
        if (useFilterMonths) {
            collection.whereIn(MONTH, filterMonths.toList())
        }

        return collection.snapshots()
            .map { snap -> snap.toObjects(MonthSummaryEntity::class.java) }
    }
}