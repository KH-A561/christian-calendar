package ru.akhilko.core.database.entity.day

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.christian_calendar.core.model.Reading
import ru.akhilko.christian_calendar.core.model.SaintInfo

@Entity(tableName = "calendar_days")
data class CalendarDayEntity(
    @PrimaryKey
    val id: String, // YYYY-MM-DD

    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: DayOfWeek,

    @ColumnInfo(name = "gregorian_day")
    val gregorianDay: Int,

    @ColumnInfo(name = "gregorian_month")
    val gregorianMonth: Int,

    @ColumnInfo(name = "gregorian_year")
    val gregorianYear: Int,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: String,

    val title: String,
    val week: String,

    // Конвертируемые поля
    @ColumnInfo(name = "liturgical_info")
    val liturgicalInfo: LiturgicalInfo,

    @ColumnInfo(name = "fasting_info")
    val fastingInfo: FastingInfo,

    val readings: List<Reading>,
    val saints: List<SaintInfo>,

    @ColumnInfo(name = "search_text")
    val searchText: String
)

/**
 * Внешняя модель для CalendarDayEntity.
 * В будущем может содержать связанные сущности.
 */
data class PopulatedCalendarDay(
    val entity: CalendarDayEntity
) {
    fun asModel() = CalendarDay(
        dayOfWeek = entity.dayOfWeek,
        gregorianDay = entity.gregorianDay,
        gregorianMonth = entity.gregorianMonth,
        gregorianYear = entity.gregorianYear,
        lastUpdated = entity.lastUpdated,
        title = entity.title,
        week = entity.week,
        liturgicalInfo = entity.liturgicalInfo,
        fastingInfo = entity.fastingInfo,
        readings = entity.readings,
        saints = entity.saints,
        searchText = entity.searchText
    )
}

fun CalendarDay.asEntity() = CalendarDayEntity(
    id = this.id,
    dayOfWeek = this.dayOfWeek,
    gregorianDay = this.gregorianDay,
    gregorianMonth = this.gregorianMonth,
    gregorianYear = this.gregorianYear,
    lastUpdated = this.lastUpdated,
    title = this.title,
    week = this.week,
    liturgicalInfo = this.liturgicalInfo,
    fastingInfo = this.fastingInfo,
    readings = this.readings,
    saints = this.saints,
    searchText = this.searchText
)
