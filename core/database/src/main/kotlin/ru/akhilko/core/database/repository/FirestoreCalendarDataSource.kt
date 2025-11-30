package ru.akhilko.core.database.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.database.entity.day.CalendarDayEntity
import javax.inject.Inject

private const val COLLECTION = "days"

class FirestoreCalendarDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getYearData(year: Int): List<CalendarDayEntity> {
        return try {
            firestore.collection(COLLECTION)
                .whereEqualTo("gregorianYear", year)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    document.toObject(FirestoreDay::class.java)?.toEntity(document.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

private data class FirestoreDay(
    val dayOfWeek: String = "",
    val gregorianDay: Int = 0,
    val gregorianMonth: Int = 0,
    val gregorianYear: Int = 0,
    val lastUpdated: String = "",
    val liturgical: FirestoreLiturgical = FirestoreLiturgical(),
    val fastingInfo: FirestoreFastingInfo = FirestoreFastingInfo(),
    val title: String = "",
    val week: String = ""
)

private data class FirestoreLiturgical(
    val color: String = "",
    val dayType: String = "",
    val importance: Int = 0
)

private data class FirestoreFastingInfo(
    val allowed: List<String> = emptyList(),
    val fastingLevel: String = ""
)

private fun FirestoreDay.toEntity(id: String): CalendarDayEntity {
    val liturgicalInfo = LiturgicalInfo(
        color = LiturgicalColor.valueOf(this.liturgical.color.uppercase()),
        dayType = DayType.valueOf(this.liturgical.dayType.uppercase()),
        importance = this.liturgical.importance
    )

    val fastingInfo = FastingInfo(
        fastingLevel = FastingLevel.valueOf(this.fastingInfo.fastingLevel.uppercase()),
        allowed = this.fastingInfo.allowed
    )

    return CalendarDayEntity(
        id = id,
        dayOfWeek = DayOfWeek.valueOf(this.dayOfWeek.uppercase()),
        gregorianDay = this.gregorianDay,
        gregorianMonth = this.gregorianMonth,
        gregorianYear = this.gregorianYear,
        lastUpdated = this.lastUpdated,
        title = this.title,
        week = this.week,
        liturgicalInfo = liturgicalInfo,
        fastingInfo = fastingInfo,
        readings = emptyList(),
        saints = emptyList(),
        searchText = "${this.title} ${this.week}".trim()
    )
}
