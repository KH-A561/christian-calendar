package ru.akhilko.week.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.format.monthGenitiveRu
import ru.akhilko.week.WeekMode
import java.time.LocalDate

@Composable
fun WeekHeader(
    weekStart: LocalDate,
    mode: WeekMode,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val weekEnd = weekStart.plusDays(6)
    val weekRange = "${weekStart.dayOfMonth} – ${weekEnd.dayOfMonth} ${monthGenitiveRu(weekEnd.monthValue)}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPrevWeek) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Предыдущая неделя")
            }
            IconButton(onClick = onNextWeek) {
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Следующая неделя")
            }
        }

        Text(
            text = weekRange,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        IconButton(onClick = onToggleMode) {
            Icon(
                imageVector = if (mode == WeekMode.LIST) Icons.Default.Search else Icons.AutoMirrored.Filled.ViewList,
                contentDescription = if (mode == WeekMode.LIST) "Режим поиска" else "Режим списка",
            )
        }
    }
}

@Preview(name = "WeekHeader - List")
@Composable
private fun WeekHeaderListPreview() {
    CalendarTheme {
        Surface {
            WeekHeader(
                weekStart = LocalDate.of(2026, 4, 20),
                mode = WeekMode.LIST,
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
            )
        }
    }
}

@Preview(name = "WeekHeader - Search")
@Composable
private fun WeekHeaderSearchPreview() {
    CalendarTheme {
        Surface {
            WeekHeader(
                weekStart = LocalDate.of(2026, 4, 20),
                mode = WeekMode.SEARCH,
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
            )
        }
    }
}
