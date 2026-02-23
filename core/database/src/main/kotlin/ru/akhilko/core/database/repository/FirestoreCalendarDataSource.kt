package ru.akhilko.core.database.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

private const val COLLECTION = "days"

class FirestoreCalendarDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getYearData(year: Int): List<Pair<String, FirestoreDay>> {
        return try {
            firestore.collection(COLLECTION)
                .whereEqualTo("gregorianYear", year)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    document.toObject(FirestoreDay::class.java)?.let {
                        document.id to it
                    }
                }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

data class FirestoreDay(
    val dayOfWeek: String = "",
    val gregorianDay: Int = 0,
    val gregorianMonth: Int = 0,
    val gregorianYear: Int = 0,
    val lastUpdated: Timestamp = Timestamp(Date(0)),
    val liturgical: FirestoreLiturgical = FirestoreLiturgical(),
    val fastingInfo: FirestoreFastingInfo = FirestoreFastingInfo(),
    val title: String = "",
    val week: String = ""
)

data class FirestoreLiturgical(
    val color: String = "",
    val dayType: String = "",
    val importance: Int = 0
)

data class FirestoreFastingInfo(
    val allowed: List<String> = emptyList(),
    val fastingLevel: String = ""
)
