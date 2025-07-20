package ru.akhilko.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toKotlinInstant
import ru.akhilko.christian_calendar.core.data.model.findByName
import ru.akhilko.christian_calendar.core.model.data.Day
import ru.akhilko.core.designsystem.component.DayTag
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.R
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DayResourceCardExpanded(
    day: Day,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Use custom label for accessibility services to communicate button's action to user.
        // Pass null for action to only override the label and not the actual action.
        modifier = modifier.semantics {
            onClick(label = clickActionLabel, action = null)
        },
    ) {
        Column {
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Column {
                    Row {
                        day.title?.let {
                            DayResourceTitle(
                                it
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Row {
                        DayResourceSubtitle(
                            day.weekDay.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DayResourceMetaData(day.oldStyleDate, day.newStyleDate, day.weekInfo)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DayResourceShortDescription(
                        day.primarySaints, day.secondarySaints, day.readings, day.fasting
                    )

                    day.tags?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        DayResourceTags(
                            tags = it
                        )
                    }
                }
            }
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
    resourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        resourceTitle.lowercase().replaceFirstChar
        { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
        style = MaterialTheme.typography.headlineSmall, modifier = modifier
    )
}

@Composable
fun DayResourceMetaData(
    oldStyleDate: Instant,
    newStyleDate: Instant,
    weekInfo: String?,
) {
    val oldStyleFormattedDate = dateFormatted(oldStyleDate)
    val newStyleFormattedDate = dateFormatted(newStyleDate)

    Text(
        if (!weekInfo.isNullOrBlank()) {
            stringResource(
                R.string.core_ui_card_meta_data_text,
                oldStyleFormattedDate, newStyleFormattedDate,
                weekInfo
            )
        } else {
            stringResource(
                R.string.core_ui_card_meta_data_text_without_week_info,
                oldStyleFormattedDate, newStyleFormattedDate
            )
        },
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
fun DayResourceShortDescription(
    primarySaints: List<String>?,
    secondarySaints: List<String>?,
    readings: Map<String, List<String>>?,
    fasting: String?
) {
    primarySaints?.let {
        Text(saintsFormatted(it), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
    }

    secondarySaints?.let {
        Text(saintsFormatted(it), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
    }

    readings?.let {
        for (entry in it.entries) {
            ReadingsFormatted(entry)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    fasting?.let {
        Text(text = fasting, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DayResourceTags(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    Row(
        // causes narrow chips
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (dayTag in tags.stream().map { tag -> findByName(tag) }) {
            DayTag(
                text = {
                    val contentDescription = stringResource(id = dayTag.getResourceId())
                    Text(
                        text = dayTag.getLabel(LocalContext.current).uppercase(Locale.getDefault()),
                        modifier = Modifier.semantics {
                            this.contentDescription = contentDescription
                        },
                    )
                },
            )
        }
    }
}

@Composable
fun ReadingsFormatted(readings: Map.Entry<String, List<String>>) {
    return Text(text = String.format("%s: %s", readings.key,
        "${readings.value}".removeSurrounding("[", "]")),
        style = MaterialTheme.typography.bodySmall)
}

@Composable
fun dateFormatted(date: Instant): String = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(date.toJavaInstant())

@Composable
fun saintsFormatted(list: List<String>): String = "$list".removeSurrounding("[", "]")

@Preview("DayResourceCardExpanded")
@Composable
private fun ExpandedDayResourcePreview() {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        CalendarTheme {
            Surface {
                DayResourceCardExpanded(
                    day = Day(
                        id = "1",
                        weekDay = DayOfWeek.WEDNESDAY,
                        oldStyleDate = dateToInstant(2025, 1, 30),
                        newStyleDate = dateToInstant(2025, 2, 12),
                        title = "Великая среда",
                        weekInfo = "12-ая по Пятидесятнице",
                        primarySaints = listOf(
                            "Собор трёх вселенских учителей и святителей: Василия Великого, Григория Богослова И Иоанна Златоустого"),
                        secondarySaints = listOf(
                            "Сщмч. Ипполита",
                            "мчч. Кенсорина и Савина",
                            "мц. Хрисии девы и с нею мчч. Фи-лика"
                        ),
                        readings = mapOf(
                            "Ряд." to listOf(
                                "2 Пет., 68 зач., III",
                                "1–18. Мк., 61 зач., XIII, 24–31"
                            ),
                            "Свтт." to listOf(
                                "Утр. – Ин., 36 зач., X, 9–16.",
                                "Лит. - Евр. 334 зач., XIII:7–16; Мф. 11 зач., V:14–19"
                            )
                        ),
                        fasting = "Вино и елей",
                        tags = listOf("FAST")
                    ),
                    onClick = {},
                )
            }
        }
    }
}

private fun dateToInstant(year: Int, month: Int, day: Int): Instant {
    return LocalDate(
        year = year,
        monthNumber = month,
        dayOfMonth = day
    ).atStartOfDayIn(timeZone = TimeZone.currentSystemDefault())
        .toJavaInstant().toKotlinInstant()
}