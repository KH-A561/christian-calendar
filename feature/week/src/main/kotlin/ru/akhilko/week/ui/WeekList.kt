package ru.akhilko.week.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun WeekList(
    days: List<CalendarDayPresentation>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = days,
            key = { it.id },
        ) { day ->
            WeekDayRow(day = day, onClick = onDayClick)
        }
    }
}
