package ru.akhilko.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale as JavaLocale

@Composable
fun MonthRoute(
    modifier: Modifier = Modifier,
    onDayClick: (String) -> Unit,
    viewModel: MonthViewModel = hiltViewModel()
) {
    val monthUiState: MonthUiState by viewModel.monthUiState.collectAsStateWithLifecycle()

    MonthScreen(
        monthUiState = monthUiState,
        onDayClick = onDayClick,
        modifier = modifier
    )
}

@Composable
internal fun MonthScreen(
    modifier: Modifier = Modifier,
    monthUiState: MonthUiState,
    onDayClick: (String) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { currentMonth.plusMonths(24) }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek() }

    val days = when (monthUiState) {
        is MonthUiState.Success -> monthUiState.days
        else -> emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.secondaryContainer),
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
                dayContent = { day ->
                    val dayResource = remember(days, day.date) {
                        days.find {
                            val resourceDate = it.day.getGregorianLocalDate()
                            resourceDate == day.date
                        }
                    }
                    Day(
                        day = day,
                        today = today,
                        dayResource = dayResource,
                        onClick = onDayClick,
                    )
                },
                monthContainer = { _, container ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
                    ) {
                        container()
                    }
                },
                monthHeader = { month ->
                    Column {
                        Text(
                            text = month.yearMonth.format(
                                DateTimeFormatter.ofPattern(
                                    "LLLL yyyy",
                                    JavaLocale("ru")
                                )
                            ).replaceFirstChar { it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        MonthHeader(daysOfWeek)
                    }
                },
                monthFooter = { month ->
                    val monthDays = remember(days, month) {
                        days.filter {
                            val resourceDate = it.day.getGregorianLocalDate()
                            resourceDate.year == month.yearMonth.year &&
                                    resourceDate.monthNumber == month.yearMonth.monthValue
                        }
                    }
                    MonthFooter(monthDays)
                }
            )
        }

        if (monthUiState is MonthUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<java.time.DayOfWeek>) {
    val containerColor = colorScheme.secondary
    Row(
        Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(vertical = 12.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                color = colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                text = dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    JavaLocale("ru")
                ),
            )
        }
    }
}

private data class LegendItem(val color: Color, val text: String)

@Composable
private fun MonthFooter(monthDays: List<CalendarDayResource>) {
    val feastColor = MaterialTheme.colorScheme.error
    val memorialColor = MaterialTheme.colorScheme.outlineVariant
    val fastingColor = MaterialTheme.colorScheme.inversePrimary

    val legendItems = remember(monthDays, feastColor, memorialColor, fastingColor) {
        val items = mutableSetOf<LegendItem>()
        if (monthDays.any { it.day.liturgicalInfo.dayType == DayType.FEAST }) {
            items.add(LegendItem(feastColor, "Великий праздник"))
        }
        if (monthDays.any { it.day.liturgicalInfo.dayType == DayType.MEMORIAL }) {
            items.add(LegendItem(memorialColor, "День поминовения"))
        }
        if (monthDays.any { it.day.fastingInfo.fastingLevel != FastingLevel.NONE }) {
            items.add(LegendItem(fastingColor, "Постный день"))
        }
        items.toList()
    }


    if (legendItems.isEmpty()) {
        Spacer(Modifier.height(20.dp))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Легенда:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val half = (legendItems.size + 1) / 2
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                legendItems.take(half).forEach { LegendRow(it) }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                legendItems.drop(half).forEach { LegendRow(it) }
            }
        }
    }
}

@Composable
private fun LegendRow(item: LegendItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(item.color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    dayResource: CalendarDayResource?,
    onClick: (String) -> Unit,
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val containerColor = colorScheme.surface
    val selectedContainerColor = colorScheme.primaryContainer
    val disabledTextColor = colorScheme.onSurface.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(if (day.date == today && isCurrentMonth) selectedContainerColor else containerColor)
            .clickable(
                enabled = isCurrentMonth && dayResource != null,
                onClick = { dayResource?.id?.let(onClick) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val indicatorColor = getIndicatorColor(dayResource)
        if (indicatorColor != null && isCurrentMonth) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(indicatorColor)
            )
        }
        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isCurrentMonth) contentColorFor(backgroundColor = containerColor)
                else disabledTextColor,
            )
        }
    }
}

@Composable
private fun getIndicatorColor(dayResource: CalendarDayResource?): Color? {
    if (dayResource == null) return null

    if (dayResource.day.liturgicalInfo.dayType == DayType.FEAST) {
        return colorScheme.error
    }

    if (dayResource.day.liturgicalInfo.dayType == DayType.MEMORIAL) {
        return colorScheme.outlineVariant
    }

    if (dayResource.day.fastingInfo.fastingLevel != FastingLevel.NONE) {
        return colorScheme.inversePrimary
    }

    return null
}

@Preview(showSystemUi = true)
@Composable
private fun MonthScreenPreview() {
    val sampleDays = listOf(
        CalendarDayResource(
            id = "1",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                dayOfWeek = kotlinx.datetime.DayOfWeek.TUESDAY,
                gregorianDay = 5,
                gregorianMonth = 11,
                gregorianYear = 2024,
                lastUpdated = "",
                title = "Предпразднство Введения во храм Пресвятой Богородицы",
                week = "Седмица 24-я по Пятидесятнице",
                liturgicalInfo = LiturgicalInfo(LiturgicalColor.VIOLET, DayType.FEAST, 3),
                fastingInfo = FastingInfo(FastingLevel.NONE, emptyList()),
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            ),
            holidays = emptyList(),
            fastingInformation = FastingInfo(FastingLevel.NONE, emptyList())
        ),
        CalendarDayResource(
            id = "2",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                dayOfWeek = kotlinx.datetime.DayOfWeek.FRIDAY,
                gregorianDay = 8,
                gregorianMonth = 11,
                gregorianYear = 2024,
                lastUpdated = "",
                title = "Отдание праздника Введения во храм Пресвятой Богородицы",
                week = "Седмица 24-я по Пятидесятнице",
                liturgicalInfo = LiturgicalInfo(LiturgicalColor.VIOLET, DayType.ORDINARY, 3),
                fastingInfo = FastingInfo(FastingLevel.STRICT, emptyList()),
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            ),
            holidays = emptyList(),
            fastingInformation = FastingInfo(FastingLevel.STRICT, emptyList())
        ),
        CalendarDayResource(
            id = "3",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                dayOfWeek = kotlinx.datetime.DayOfWeek.SATURDAY,
                gregorianDay = 9,
                gregorianMonth = 11,
                gregorianYear = 2024,
                lastUpdated = "",
                title = "Освящение церкви вмч. Георгия в Киеве",
                week = "Седмица 24-я по Пятидесятнице",
                liturgicalInfo = LiturgicalInfo(LiturgicalColor.VIOLET, DayType.MEMORIAL, 3),
                fastingInfo = FastingInfo(FastingLevel.NONE, emptyList()),
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            ),
            holidays = emptyList(),
            fastingInformation = FastingInfo(FastingLevel.NONE, emptyList())
        )
    )

    CalendarTheme {
        MonthScreen(
            monthUiState = MonthUiState.Success(
                centeredMonth = YearMonth.now(),
                daySelected = null,
                days = sampleDays
            ),
            onDayClick = {}
        )
    }
}
