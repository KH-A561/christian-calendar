package ru.akhilko.core.calendarsystem

import androidx.compose.runtime.Immutable
import java.io.Serializable
import java.time.LocalDate

@Immutable
data class CalendarDay(val date: LocalDate, val position: DayPosition) : Serializable