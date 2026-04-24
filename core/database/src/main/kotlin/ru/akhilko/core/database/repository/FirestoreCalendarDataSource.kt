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

    suspend fun getDeltaUpdates(sinceMillis: Long): List<Pair<String, FirestoreDay>> {
        val sinceTimestamp = Timestamp(Date(sinceMillis))
        return try {
            firestore.collection(COLLECTION)
                .whereGreaterThan("lastUpdated", sinceTimestamp)
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
    val dayTypes: List<String> = emptyList(),
    val liturgical: FirestoreLiturgical = FirestoreLiturgical(),
    val fastingInfo: FirestoreFastingInfo = FirestoreFastingInfo(),
    val title: String = "",
    val week: String = "",
    val readings: List<String> = emptyList(),
    val saints: List<String> = emptyList()
)

data class FirestoreLiturgical(
    val importance: Int = 0
)

data class FirestoreFastingInfo(
    val allowed: List<String> = emptyList(),
    val fastingLevel: String = "",
    val fastingName: String? = null
)
