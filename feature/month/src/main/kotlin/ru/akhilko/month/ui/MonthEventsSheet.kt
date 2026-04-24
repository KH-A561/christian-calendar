package ru.akhilko.month.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarMonth
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import ru.akhilko.core.ui.format.monthNominativeRu

/**
 * Модальный bottom sheet со всем списком «событий месяца».
 * Вынесен из [MonthEventSummary] наружу, чтобы его внутренний скролл
 * не конфликтовал со snap-flingом `VerticalCalendar` (paged-режим).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthEventsSheet(
    month: CalendarMonth,
    summaries: Map<Int, List<MonthSummary>>,
    dayResources: List<CalendarDayResource>,
    onEventClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val yearMonth = month.yearMonth
    val summary = summaries[yearMonth.year]?.find { it.month == yearMonth.monthValue }
    val rows = summary?.let { monthEventRows(it, dayResources, yearMonth) }.orEmpty()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "События · ${monthNominativeRu(yearMonth.monthValue)} ${yearMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(rows, key = { it.navigateId + it.dateLabel }) { row ->
                    EventRow(
                        dateLabel = row.dateLabel,
                        title = row.title,
                        color = row.color,
                        onClick = { onEventClick(row.navigateId) },
                    )
                }
            }
        }
    }
}
