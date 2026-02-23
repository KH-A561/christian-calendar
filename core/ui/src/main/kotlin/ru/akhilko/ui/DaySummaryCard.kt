package ru.akhilko.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySummaryCard(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val liturgicalColor = when (day.liturgicalInfo.color) {
        LiturgicalColor.RED -> Color(0xFFB71C1C)
        LiturgicalColor.BLUE -> Color(0xFF64B5F6)
        LiturgicalColor.GOLD -> Color(0xFFFFD700)
        LiturgicalColor.GREEN -> Color(0xFF81C784)
        LiturgicalColor.PURPLE -> Color(0xFF9575CD)
        LiturgicalColor.BLACK -> Color.Black
        LiturgicalColor.WHITE -> Color.White
        LiturgicalColor.NONE -> Color.Transparent
    }

    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 6.dp.toPx() // Increased from 4.dp
                drawLine(
                    color = liturgicalColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 20.dp, // Increased from 16.dp
                    end = 16.dp, // Increased from 12.dp
                    top = 16.dp, // Increased from 12.dp
                    bottom = 16.dp // Increased from 12.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${day.gregorianDay} ${
                        day.gregorianMonth.let {
                            java.time.Month.of(it).getDisplayName(
                                java.time.format.TextStyle.FULL,
                                Locale("ru")
                            )
                        }
                    } / ${day.julianDay} ${
                        day.julianMonth.let {
                            java.time.Month.of(it).getDisplayName(
                                java.time.format.TextStyle.FULL,
                                Locale("ru")
                            )
                        }
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = day.dayOfWeek
                        .getDisplayName(java.time.format.TextStyle.FULL, Locale("ru"))
                        .uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = day.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (day.week.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = day.week,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))

            if (day.fastingInfo.fastingLevel != FastingLevel.NONE) {

                Text(
                    text = "Пост: ${day.fastingInfo.fastingName ?: "Да"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (day.fastingInfo.allowed.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Разрешено: ${day.fastingInfo.allowed.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "Поста нет",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
private fun DaySummaryCardPreview() {
    val sampleDay = CalendarDay(
        dayOfWeek = DayOfWeek.WEDNESDAY,
        gregorianDay = 12,
        gregorianMonth = 2,
        gregorianYear = 2025,
        julianDay = 30,
        julianMonth = 1,
        julianYear = 2025,
        lastUpdated = "21 August 2025 at 17:53:08 UTC+3",
        title = "Предпразднство Преображения Господня",
        week = "Седмица 11-я по Пятидесятнице.",
        dayTypes = listOf(DayType.FOREFEAST),
        liturgicalInfo = LiturgicalInfo(
            color = LiturgicalColor.PURPLE,
            importance = 3
        ),
        fastingInfo = FastingInfo(
            fastingLevel = FastingLevel.PARTIAL,
            allowed = listOf("Вино", "Елей"),
            fastingName = "Успенский пост"
        ),
        readings = emptyList(),
        saints = emptyList(),
        searchText = ""
    )

    CalendarTheme {
        Column(modifier = Modifier.padding(16.dp)) { // Increased padding
            DaySummaryCard(day = sampleDay, onClick = {})
        }
    }
}