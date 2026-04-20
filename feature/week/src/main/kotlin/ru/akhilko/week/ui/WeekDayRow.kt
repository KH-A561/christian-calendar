package ru.akhilko.week.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.DayTypeBadgeRow
import ru.akhilko.core.designsystem.component.JulianDateLabel
import ru.akhilko.core.designsystem.component.JulianStyle
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun WeekDayRow(
    day: CalendarDayPresentation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Left Box: Date and Weekday
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Text(
                text = day.gregorianDayNum.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (day.isToday) TextDecoration.Underline else null
                ),
                color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = day.weekdayShortRu.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right Box: Title, Saints, Readings, Badges
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = day.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (day.saints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.saints.take(2).joinToString("; "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (day.readings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.readings.take(2).joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DayTypeBadgeRow(kinds = day.badges)

                if (day.badges.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                JulianDateLabel(
                    julianYear = day.julianYear,
                    julianMonth = day.julianMonth,
                    julianDay = day.julianDay,
                    style = JulianStyle.Inline
                )
            }
        }
    }
}
