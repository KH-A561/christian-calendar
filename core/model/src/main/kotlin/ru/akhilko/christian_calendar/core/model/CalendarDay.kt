package ru.akhilko.christian_calendar.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class CalendarDay(
    // Основные поля из документа
    val dayOfWeek: DayOfWeek,
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val lastUpdated: String, // Firestore возвращает строку
    val title: String,
    val week: String,

    // Вложенные объекты
    val liturgicalInfo: LiturgicalInfo,
    val fastingInfo: FastingInfo,

    // Данные из сабколлекций
    val readings: List<Reading>,
    val saints: List<SaintInfo>,

    // Конкатенированный текст для поиска
    val searchText: String
) {
    /**
     * Уникальный идентификатор дня в формате YYYY-MM-DD.
     * Генерируется на основе григорианской даты.
     */
    val id: String
        get() = "${gregorianYear}-${gregorianMonth.toString().padStart(2, '0')}-${gregorianDay.toString().padStart(2, '0')}"

    /**
     * Преобразует григорианскую дату в объект LocalDate.
     */
    fun getGregorianLocalDate(): LocalDate {
        return LocalDate(gregorianYear, gregorianMonth, gregorianDay)
    }
}
