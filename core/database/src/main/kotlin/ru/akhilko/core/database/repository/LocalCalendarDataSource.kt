
package ru.akhilko.core.database.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.akhilko.core.database.dto.CalendarDayDto
import javax.inject.Inject

class LocalCalendarDataSource @Inject constructor(
    private val context: Context,
    private val json: Json,
) {
    suspend fun getCalendarData(): List<CalendarDayDto> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("calendar.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<CalendarDayDto>>(jsonString)
    }
}
