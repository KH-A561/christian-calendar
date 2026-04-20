package ru.akhilko.week.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    val weekEnd = remember(weekStart) { weekStart.plusDays(6) }

    val text = remember(weekStart, weekEnd) {
        val startMonth = monthGenitiveRu(weekStart.monthValue)
        val endMonth = monthGenitiveRu(weekEnd.monthValue)

        if (weekStart.monthValue == weekEnd.monthValue) {
            "${weekStart.dayOfMonth} – ${weekEnd.dayOfMonth} $startMonth"
        } else {
            "${weekStart.dayOfMonth} $startMonth – ${weekEnd.dayOfMonth} $endMonth"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevWeek) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous week")
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onNextWeek) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next week")
        }

        IconButton(onClick = onToggleMode) {
            Icon(
                imageVector = if (mode == WeekMode.LIST) Icons.Default.Search else Icons.AutoMirrored.Filled.List,
                contentDescription = "Toggle mode"
            )
        }
    }
}
