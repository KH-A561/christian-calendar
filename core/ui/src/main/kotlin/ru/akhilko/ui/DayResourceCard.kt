package ru.akhilko.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaZoneId
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.christian_calendar.core.model.Reading
import ru.akhilko.christian_calendar.core.model.ReadingType
import ru.akhilko.christian_calendar.core.model.SaintInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.R
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.DayOfWeek as KotlinxDayOfWeek

@Composable
fun DayResourceCardExpanded(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.semantics {
            onClick(label = clickActionLabel, action = null)
        },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            DayResourceTitle(day.title)
            Spacer(modifier = Modifier.height(12.dp))

            DayResourceSubtitle(
                day.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            )
            Spacer(modifier = Modifier.height(14.dp))

            DayResourceMetaData(
                date = day.getGregorianLocalDate(),
                weekInfo = day.week
            )
            Spacer(modifier = Modifier.height(14.dp))

            DayResourceShortDescription(
                saints = day.saints,
                readings = day.readings,
                fastingInfo = day.fastingInfo
            )
        }
    }
}

@Composable
fun DayResourceTitle(
    resourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(resourceTitle, style = MaterialTheme.typography.headlineSmall, modifier = modifier)
}

@Composable
fun DayResourceSubtitle(
    resourceSubtitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        resourceSubtitle.replaceFirstChar { it.titlecase(Locale.getDefault()) },
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}

@Composable
fun DayResourceMetaData(
    date: LocalDate,
    weekInfo: String,
) {
    val formattedDate = dateFormatted(date)
    val text = if (weekInfo.isNotBlank()) {
        "$formattedDate, $weekInfo"
    } else {
        formattedDate
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
fun DayResourceShortDescription(
    saints: List<SaintInfo>,
    readings: List<Reading>,
    fastingInfo: FastingInfo
) {
    if (saints.isNotEmpty()) {
        Text(saintsFormatted(saints), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (readings.isNotEmpty()) {
        Column {
            for (reading in readings) {
                ReadingsFormatted(reading)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (fastingInfo.fastingLevel != FastingLevel.NONE) {
        FastingInfoFormatted(fastingInfo)
    }
}

@Composable
fun FastingInfoFormatted(fastingInfo: FastingInfo) {
    val fastingText = when (fastingInfo.fastingLevel) {
        FastingLevel.STRICT -> "Строгий пост"
        FastingLevel.PARTIAL -> {
            if (fastingInfo.allowed.isNotEmpty()) {
                "Разрешено: ${fastingInfo.allowed.joinToString(separator = ", ")}"
            } else {
                // Текст по умолчанию, если вдруг список `allowed` окажется пустым
                "Пост с послаблениями"
            }
        }
        FastingLevel.NONE -> "" // Не будет вызвано из-за проверки выше, но для полноты
    }
    if (fastingText.isNotBlank()) {
        Text(text = fastingText, style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
fun ReadingsFormatted(reading: Reading) {
    Text(
        text = "${reading.description}: ${reading.passage}",
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun dateFormatted(date: LocalDate): String {
    val instant = date.atStartOfDayIn(TimeZone.currentSystemDefault())
    val javaInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
    return DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(TimeZone.currentSystemDefault().toJavaZoneId())
        .format(javaInstant)
}

@Composable
fun saintsFormatted(saints: List<SaintInfo>): String =
    saints.flatMap { it.names }.joinToString("\n")

@Preview("DayResourceCardExpanded")
@Composable
private fun ExpandedDayResourcePreview() {
    val sampleDay = CalendarDay(
        dayOfWeek = KotlinxDayOfWeek.WEDNESDAY,
        gregorianDay = 12,
        gregorianMonth = 2,
        gregorianYear = 2025,
        lastUpdated = "21 August 2025 at 17:53:08 UTC+3",
        title = "Предпразднство Преображения Господня",
        week = "Седмица 11-я по Пятидесятнице.",
        liturgicalInfo = LiturgicalInfo(
            color = LiturgicalColor.VIOLET,
            dayType = DayType.FEAST,
            importance = 3
        ),
        fastingInfo = FastingInfo(
            fastingLevel = FastingLevel.PARTIAL,
            allowed = listOf("Вино", "Елей")
        ),
        readings = listOf(
            Reading(
                type = ReadingType.GOSPEL,
                passage = "Мф. 10:1-8",
                description = "Евангелие утреннее"
            ),
            Reading(
                type = ReadingType.APOSTLE,
                passage = "Деян. 5:12-20",
                description = "Апостол"
            )
        ),
        saints = listOf(
            SaintInfo(names = listOf("АП. И ЕВАНГЕЛИСТА МАТФЕЯ (60)")),
            SaintInfo(names = listOf("Сщмч. Ипполита", "мчч. Кенсорина и Савина"))
        ),
        searchText = ""
    )

    CalendarTheme {
        Surface {
            Row(modifier = Modifier.padding(16.dp)) {
                DayResourceCardExpanded(
                    day = sampleDay,
                    onClick = {},
                )
            }
        }
    }
}