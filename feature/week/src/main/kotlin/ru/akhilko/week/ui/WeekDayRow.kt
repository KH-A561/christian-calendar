package ru.akhilko.week.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.component.DayTypeBadgeRow
import ru.akhilko.core.designsystem.component.FastingChip
import ru.akhilko.core.designsystem.component.JulianDateLabel
import ru.akhilko.core.designsystem.component.JulianStyle
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun WeekDayRow(
    day: CalendarDayPresentation,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = if (day.isToday) 3.dp else 1.dp,
        color = if (day.isToday) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(day.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = if (compact) 10.dp else 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = day.gregorianDayNum.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (day.isToday) TextDecoration.Underline else TextDecoration.None,
                    )
                    Text(
                        text = day.weekdayShortRu,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!compact) {
                    day.saints.take(2).forEach { saint ->
                        Text(
                            text = saint,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    day.readings.take(2).forEach { reading ->
                        Text(
                            text = reading,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    val compactSubtitle = listOfNotNull(
                        day.saints.firstOrNull(),
                        day.readings.firstOrNull(),
                    ).joinToString(" • ")
                    if (compactSubtitle.isNotBlank()) {
                        Text(
                            text = compactSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                DayTypeBadgeRow(kinds = day.badges)

                day.fastingName?.let {
                    FastingChip(text = it)
                }

                JulianDateLabel(
                    julianYear = day.julianYear,
                    julianMonth = day.julianMonth,
                    julianDay = day.julianDay,
                    style = JulianStyle.Inline,
                )

                val weekText = day.weekText
                if (!compact && !weekText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = weekText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(name = "WeekDayRow - Full")
@Composable
private fun WeekDayRowPreview() {
    CalendarTheme {
        Surface {
            WeekDayRow(
                day = previewDay(isToday = true),
                onClick = {},
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Preview(name = "WeekDayRow - Compact")
@Composable
private fun WeekDayRowCompactPreview() {
    CalendarTheme {
        Surface {
            WeekDayRow(
                day = previewDay(isToday = false),
                onClick = {},
                compact = true,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

private fun previewDay(isToday: Boolean): CalendarDayPresentation = CalendarDayPresentation(
    id = "2026-04-20",
    gregorianDayNum = 20,
    gregorianMonth = 4,
    gregorianYear = 2026,
    julianDay = 7,
    julianMonth = 4,
    julianYear = 2026,
    monthYearRu = "Апрель 2026",
    weekdayRu = "Понедельник",
    weekdayShortRu = "Пн",
    badges = listOf(BadgeKind.GREAT, BadgeKind.FAST),
    title = "Собор преподобных отцов",
    weekText = "Светлая седмица",
    fastingLevelRu = "Поста нет",
    fastingName = "Петров пост",
    allowed = listOf("Рыба"),
    readings = listOf("Ин. 10:1-9", "Евр. 7:26-8:2"),
    saints = listOf("Прп. Сергий Радонежский", "Свт. Николай Чудотворец"),
    isToday = isToday,
)
