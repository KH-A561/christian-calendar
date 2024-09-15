package ru.akhilko.core.calendarsystem

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class CalendarWeek(val weekDays: List<CalendarDay>) : Serializable