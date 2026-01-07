
package ru.akhilko.core.database.repository

import android.content.Context
import kotlinx.serialization.json.Json
import ru.akhilko.core.database.dto.CalendarDayDto
import javax.inject.Inject

class LocalCalendarDataSource @Inject constructor(
    private val context: Context,
    private val json: Json,
) {
    fun getCalendarData(): List<CalendarDayDto> {
        val jsonString = context.assets.open("calendar.json").bufferedReader().use { it.readText() }
        return json.decodeFromString<List<CalendarDayDto>>(jsonString)
    }
}
