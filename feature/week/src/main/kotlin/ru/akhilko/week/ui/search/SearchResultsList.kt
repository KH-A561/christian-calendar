package ru.akhilko.week.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.week.ui.WeekDayRow

@Composable
fun SearchResultsList(
    results: List<CalendarDayPresentation>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = results,
            key = { it.id }
        ) { day ->
            WeekDayRow(
                day = day,
                onClick = { onDayClick(day.id) }
            )
        }
    }
}
