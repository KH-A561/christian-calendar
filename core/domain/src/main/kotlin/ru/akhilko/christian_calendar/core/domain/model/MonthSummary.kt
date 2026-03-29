package ru.akhilko.christian_calendar.core.domain.model

data class MonthSummary(
    val year: Int,
    val month: Int,
    val highlightedDays: List<HighlightedDay>,
    val highlightedPeriods: List<HighlightedPeriod>
) {
    fun isEmpty() : Boolean {
        return highlightedDays.isEmpty() && highlightedPeriods.isEmpty()
    }
}

data class HighlightedDay(
    val dayOfMonth: Int,
    val name: String
)

data class HighlightedPeriod(
    val startDay: Int,
    val endDay: Int,
    val name: String
)
