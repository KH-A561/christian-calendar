package ru.akhilko.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.datetime.DayOfWeek
import ru.akhilko.core.designsystem.theme.CalendarTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@Composable
internal fun MonthRoute(
    modifier: Modifier = Modifier,
    viewModel: MonthViewModel = hiltViewModel()
) {
    val monthUiState: MonthUiState by viewModel.monthUiState.collectAsStateWithLifecycle()

    MonthScreen(
        monthUiState = monthUiState,
        modifier = modifier
    )
}

@Composable
internal fun MonthScreen(
    modifier: Modifier = Modifier,
    selectedDate: CalendarDay = CalendarDay(LocalDate.now(), DayPosition.MonthDate),
    monthUiState: MonthUiState
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column {
            val state = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = daysOfWeek.first(),
            )
            VerticalCalendar(
                state = state,
                contentPadding = PaddingValues(bottom = 100.dp),
                dayContent = { value ->
                    Day(
                        value,
                        today = today,
                    ) {}
                },
                monthContainer = { _, container ->
                    Card(
                        onClick = {},
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
                    ) {
                        container()
                    }
                },
                monthHeader = { _ -> MonthHeader(daysOfWeek) },
                monthFooter = { month ->
                    MonthFooter(month)
                }
            )
        }
    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    val containerColor = MaterialTheme.colorScheme.secondary
    Row(
        Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(vertical = 12.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                text = dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.current.platformLocale
                ),
            )
        }
    }
}

@Composable
private fun MonthFooter(calendarMonth: CalendarMonth) {
    val containerColor = MaterialTheme.colorScheme.surface
    Row {

        Column(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                .background(containerColor),
        ) {
            Row(modifier = Modifier.padding(end = 12.dp)) {
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .height(7.dp)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Пост",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .height(7.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(4.dp))

                Text(
                    text = "Праздник",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                .background(containerColor),
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .height(7.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(4.dp))

                Text(
                    text = "Родительский день",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .height(7.dp)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(4.dp))

                Text(
                    text = "Успение Богородицы",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    onClick: (CalendarDay) -> Unit,
) {
    val isDayInMonth = isDayInMonth(day, today)
    val containerColor = MaterialTheme.colorScheme.surface
    val selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
    val disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 0.5f,
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .background(if (day.date == today) selectedContainerColor else containerColor)
            .clickable(
                enabled = isDayInMonth,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (day.date.dayOfMonth == 15) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
        if (day.date.dayOfMonth == 19) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.inversePrimary)
            )
        }
        if (day.date.dayOfMonth == 23) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isDayInMonth) contentColorFor(backgroundColor = containerColor)
                else disabledContainerColor,
            )
        }
    }
}

@Composable
private fun isDayInMonth(
    day: CalendarDay,
    today: LocalDate
) = day.position == DayPosition.MonthDate && day.date >= today

@Preview
@Composable
private fun EmptyStatePreview() {
    CalendarTheme {
        MonthScreen(monthUiState = MonthUiState.Loading)
    }
}