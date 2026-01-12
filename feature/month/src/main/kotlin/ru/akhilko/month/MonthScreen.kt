package ru.akhilko.month

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.core.designsystem.theme.CalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale as JavaLocale

// Цвета
private val ColorGreatFeast = Color(0xFFB71C1C) // Кроваво-красный
private val ColorFeast = Color(0xFFF06292)      // Розовый
private val ColorFast = Color(0xFF9575CD)       // Фиолетово-серый
private val ColorMemorial = Color(0xFF64B5F6)   // Голубовато-серый

@Composable
fun MonthRoute(
    modifier: Modifier = Modifier,
    onDayClick: (String) -> Unit,
    viewModel: MonthViewModel
) {
    val monthUiState: MonthUiState by viewModel.monthUiState.collectAsStateWithLifecycle()

    MonthScreen(
        monthUiState = monthUiState,
        onDayClick = onDayClick,
        scrollToToday = viewModel.scrollToTodayRequested,
        modifier = modifier
    )
}

@Composable
internal fun MonthScreen(
    modifier: Modifier = Modifier,
    monthUiState: MonthUiState,
    onDayClick: (String) -> Unit,
    scrollToToday: Flow<Unit>
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { currentMonth.plusMonths(24) }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val scope = rememberCoroutineScope()

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )

    LaunchedEffect(Unit) {
        scrollToToday.collect {
            scope.launch {
                state.scrollToMonth(today.yearMonth)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (monthUiState) {
            MonthUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(32.dp),
                    strokeWidth = 3.dp
                )
            }
            is MonthUiState.Success -> {
                val days = monthUiState.days

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.surface)
                ) {
                    MonthDaysOfWeekHeader(daysOfWeek)
                    VerticalCalendar(
                        state = state,
                        userScrollEnabled = true,
                        calendarScrollPaged = true,
                        contentPadding = PaddingValues(bottom = 32.dp),
                        dayContent = { day ->
                            val dayResource = days.find {
                                it.day.getGregorianLocalDate() == day.date
                            }
                            Day(
                                day = day,
                                today = today,
                                dayResource = dayResource,
                                onClick = onDayClick,
                            )
                        },
                        monthHeader = { month ->
                            MonthLabel(month.yearMonth)
                        },
                        monthContainer = { _, container ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                container()
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                                    thickness = 1.dp,
                                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        },
                        monthFooter = { month ->
                            val monthDays = days.filter {
                                val date = it.day.getGregorianLocalDate()
                                date.year == month.yearMonth.year &&
                                        date.monthNumber == month.yearMonth.monthValue
                            }
                            if (monthDays.isNotEmpty()) {
                                MonthEventSummary(monthDays, onDayClick)
                            }
                        }
                    )
                }
            }
            MonthUiState.Error -> {
                 Text(
                    text = "Ошибка загрузки данных",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun MonthLabel(yearMonth: YearMonth) {
    Text(
        text = yearMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy", JavaLocale("ru")))
            .replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
        color = colorScheme.onSurface
    )
}

@Composable
private fun MonthEventSummary(
    monthDays: List<CalendarDayResource>,
    onDayClick: (String) -> Unit
) {
    val importantDays = monthDays.filter { 
        it.day.liturgicalInfo.importance >= 2 || it.day.liturgicalInfo.dayType == DayType.FEAST 
    }.sortedBy { it.day.gregorianDay }

    if (importantDays.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "События месяца",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        importantDays.take(4).forEach { day ->
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDayClick(day.id) }
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(getHighlightColor(day))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${day.day.gregorianDay}: ${day.day.title}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (day.day.liturgicalInfo.importance >= 3) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun getHighlightColor(day: CalendarDayResource): Color {
    return when {
        day.day.liturgicalInfo.importance >= 3 -> ColorGreatFeast
        day.day.liturgicalInfo.dayType == DayType.FEAST -> ColorFeast
        day.day.liturgicalInfo.dayType == DayType.MEMORIAL -> ColorMemorial
        day.day.fastingInfo.fastingLevel != FastingLevel.NONE -> ColorFast
        else -> Color.Transparent
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
    val isToday = day.date == today && isCurrentMonth
    
    val dayType = dayResource?.day?.liturgicalInfo?.dayType
    val importance = dayResource?.day?.liturgicalInfo?.importance ?: 0
    val isFast = dayResource != null && (dayResource.day.fastingInfo.fastingLevel != FastingLevel.NONE || dayType == DayType.FAST)

    // Фон для праздников
    val backgroundColor = when {
        !isCurrentMonth -> Color.Transparent
        importance >= 3 -> ColorGreatFeast.copy(alpha = 0.12f)
        dayType == DayType.FEAST -> ColorFeast.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val textColor = when {
        !isCurrentMonth -> colorScheme.onSurface.copy(alpha = 0.25f)
        importance >= 3 -> ColorGreatFeast
        dayType == DayType.FEAST -> ColorFeast
        day.date.dayOfWeek == DayOfWeek.SUNDAY -> ColorGreatFeast.copy(alpha = 0.6f)
        else -> colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = isCurrentMonth,
                onClick = { dayResource?.id?.let(onClick) ?: onClick(day.date.toString()) },
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = if (importance >= 2 || isToday) FontWeight.Black else FontWeight.Normal
            )
            
            // Полоски под числом (индикаторы)
            if (isCurrentMonth && (isFast || dayType == DayType.MEMORIAL)) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.6f).padding(top = 1.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isFast) {
                        Box(Modifier.height(3.dp).weight(1f).background(ColorFast, RoundedCornerShape(1.dp)))
                    }
                    if (dayType == DayType.MEMORIAL) {
                        if (isFast) Spacer(Modifier.width(2.dp))
                        Box(Modifier.height(3.dp).weight(1f).background(ColorMemorial, RoundedCornerShape(1.dp)))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDaysOfWeekHeader(daysOfWeek: List<DayOfWeek>) {
    Surface(tonalElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, JavaLocale("ru")).uppercase(),
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MonthScreenPreview() {
    CalendarTheme {
        MonthScreen(
            monthUiState = MonthUiState.Success(
                centeredMonth = YearMonth.now(),
                daySelected = null,
                days = emptyList()
            ),
            onDayClick = {},
            scrollToToday = emptyFlow()
        )
    }
}
