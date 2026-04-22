package ru.akhilko.week.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.component.DayTypeBadgeRow
import ru.akhilko.core.designsystem.component.FastingChip
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorFeast
import ru.akhilko.core.designsystem.theme.ColorGreat
import ru.akhilko.core.designsystem.theme.ColorRemembrance
import ru.akhilko.core.ui.format.monthGenitiveRu
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekDayRow(
    day: CalendarDayPresentation,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val leftBg = when {
        day.badges.contains(BadgeKind.GREAT) -> ColorGreat.copy(alpha = 0.18f)
        day.badges.contains(BadgeKind.FEAST) -> ColorFeast.copy(alpha = 0.18f)
        day.badges.contains(BadgeKind.REMEMBRANCE) -> ColorRemembrance.copy(alpha = 0.18f)
        day.badges.contains(BadgeKind.FAST) -> ColorFast.copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surface
    }
    val visibleBadges = if (day.fastingName != null) {
        day.badges.filter { it != BadgeKind.FAST }
    } else {
        day.badges
    }
    val title = day.title.ifBlank { day.weekText.orEmpty() }
    val showSubtitle = day.title.isNotBlank() &&
        day.weekText != null &&
        day.weekText != day.title

    Card(
        onClick = { onDayClick(day.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .fillMaxHeight()
                    .background(leftBg)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = day.weekdayShortRu.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = day.gregorianDayNum.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (day.isToday) TextDecoration.Underline else null,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (visibleBadges.isNotEmpty()) {
                    DayTypeBadgeRow(kinds = visibleBadges)
                }

                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (showSubtitle) {
                    Text(
                        text = day.weekText.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                day.saints.take(2).forEach { saint ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(ColorGreat),
                        )
                        Text(
                            text = saint,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (day.readings.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                if (day.readings.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        day.readings.forEach { reading ->
                            Text(
                                text = reading,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }

                day.fastingName?.let { FastingChip(it) }

                Text(
                    text = "${day.julianDay} ${monthGenitiveRu(day.julianMonth)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                onDayClick = {},
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
