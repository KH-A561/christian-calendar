@file:OptIn(ExperimentalLayoutApi::class)

package ru.akhilko.christian_calendar.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ru.akhilko.christian_calendar.R
import ru.akhilko.christian_calendar.model.Day
import ru.akhilko.christian_calendar.utils.readDayFromJson
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val CALENDAR_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy")
private const val TWO_DATES_FORMAT = "%s • %s"

@Composable
fun DayCard(
    day: Day, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        )
    ) {
        DayMainInfoCard(day, modifier)
        DayDetailsCard(day)
    }
}

@Composable
fun DayMainInfoCard(
    day: Day, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column {
            Text(
                modifier = Modifier.padding(8.dp),
                text = day.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = TextUnit(24.0F, TextUnitType.Sp)
            )

            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0f),
                    text = day.calendarDate.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    ).capitalize(Locale.getDefault()),
                    fontSize = TextUnit(16.0F, TextUnitType.Sp),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    modifier = Modifier.padding(8.dp), text = TWO_DATES_FORMAT.format(
                        day.calendarDate.julianDate.format(CALENDAR_DATE_FORMATTER),
                        day.calendarDate.gregorianDate.format(CALENDAR_DATE_FORMATTER)
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(16.0F, TextUnitType.Sp),
                    fontWeight = FontWeight.Normal
                )

            }

            FlowRow {
                day.types.forEach { dayType ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(dayType.title, fontWeight = FontWeight.Normal) },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayDetailsCard(day: Day) {
    Column {
        day.details.forEach { dayDetail ->
            Text(
                text = dayDetail,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Start
            )
        }
        day.readings.forEach { reading ->
            Text(
                text = stringResource(id = R.string.readings_format)
                    .format(reading.key.title, reading.value)
            )
        }
    }
}

@Preview
@Composable
private fun DayCardPreview() {
    DayCard(
        readDayFromJson()
    )
}
