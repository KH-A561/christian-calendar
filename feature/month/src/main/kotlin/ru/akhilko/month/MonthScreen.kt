package ru.akhilko.month

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.domain.GenerateMonthSummariesUseCase
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorFeast
import ru.akhilko.core.designsystem.theme.ColorMemorial
import ru.akhilko.ui.DaySummaryCard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import kotlin.math.roundToInt
import java.util.Locale as JavaLocale

// ============== Стилизация дней ============== //
// Семантические цвета (ColorFeast / ColorFast / ColorMemorial) импортируются из
// core.designsystem.theme — единый источник правды, совпадает с дизайн-доком.

private data class DayStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val fontWeight: FontWeight,
)

@Composable
private fun resolveDayStyle(
    day: CalendarDay,
    dayResource: CalendarDayResource?,
    isCurrentMonth: Boolean,
    isToday: Boolean,
): DayStyle {
    val dayTypes = dayResource?.day?.dayTypes ?: emptyList()

    // Фон: только многодневный пост — лёгкий тон, чтобы показать «зону поста».
    // Праздники и однодневные посты обозначаются полосками, не фоном.
    val backgroundColor = when {
        !isCurrentMonth -> Color.Transparent
        dayTypes.contains(DayType.LONG_FAST) -> ColorFast.copy(alpha = 0.07f)
        else -> Color.Transparent
    }

    // Цвет текста = тип праздника (главный сигнал).
    // Многодневный пост НЕ окрашивает текст — у него фон.
    val textColor = when {
        !isCurrentMonth -> colorScheme.onSurface.copy(alpha = 0.25f)
        dayTypes.any { dayType -> dayType.isFeast() } -> ColorFeast
        dayTypes.contains(DayType.COMMEMORATION) -> ColorMemorial
        // Воскресенье — «малая пасха»: окрашиваем праздничным бордовым, не розовым.
        day.date.dayOfWeek == DayOfWeek.SUNDAY -> ColorFeast
        else -> colorScheme.onSurface
    }

    val fontWeight = when {
        isToday -> FontWeight.Black
        dayTypes.any { dayType -> dayType.isFeast() } -> FontWeight.Bold
        day.date.dayOfWeek == DayOfWeek.SUNDAY -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }

    return DayStyle(backgroundColor, textColor, fontWeight)
}

// =========================================== //

@Composable
fun MonthRoute(
    modifier: Modifier = Modifier,
    onDayClick: (String) -> Unit,
    viewModel: MonthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val monthUiState: MonthScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    MonthScreen(
        monthUiState = monthUiState,
        onDayClick = onDayClick,
        scrollToToday = viewModel.scrollToTodayRequested,
        onVisibleYearChanged = viewModel::onVisibleYearChanged,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MonthScreen(
    modifier: Modifier = Modifier,
    monthUiState: MonthScreenUiState,
    onDayClick: (String) -> Unit,
    scrollToToday: Flow<Unit>,
    onVisibleYearChanged: (Int) -> Unit,
    initialSelectedDay: CalendarDayResource? = null,
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

    val visibleMonth by remember {
        derivedStateOf { state.firstVisibleMonth.yearMonth }
    }
    LaunchedEffect(visibleMonth) {
        onVisibleYearChanged(visibleMonth.year)
    }

    LaunchedEffect(Unit) {
        scrollToToday.collect {
            scope.launch {
                state.scrollToMonth(today.yearMonth)
            }
        }
    }

    var selectedDay: CalendarDayResource? by remember { mutableStateOf(initialSelectedDay) }

    if (selectedDay != null) {
        BackHandler { selectedDay = null }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (monthUiState) {
            is MonthScreenUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp),
                    strokeWidth = 3.dp,
                )
            }

            is MonthScreenUiState.Success -> {
                val days = monthUiState.days

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                ) {
                    MonthDaysOfWeekHeader(daysOfWeek)
                    VerticalCalendar(
                        state = state,
                        userScrollEnabled = true,
                        calendarScrollPaged = true,
                        contentPadding = PaddingValues(bottom = 32.dp),
                        dayContent = { day ->
                            val dayResource = days.find {
                                it.day.getGregorianLocalDate() == day.date.toKotlinLocalDate()
                            }
                            Day(
                                day = day,
                                today = today,
                                dayResource = dayResource,
                                onClick = { dayRes ->
                                    selectedDay = if (selectedDay == dayRes) null else dayRes
                                },
                            )
                        },
                        monthHeader = { month ->
                            MonthLabel(month.yearMonth)
                        },
                        monthContainer = { _, container ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                            ) {
                                container()
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp
                                    ),
                                    thickness = 1.dp,
                                    color = colorScheme.outlineVariant.copy(alpha = 0.5f),
                                )
                            }
                        },
                        monthFooter = { month ->
                            MonthEventSummary(
                                summaries = monthUiState.summaries,
                                month = month
                            )
                        },
                    )
                }
            }

            is MonthScreenUiState.Error -> {
                Text(
                    text = "Ошибка загрузки данных",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.error,
                )
            }
        }

        val dismissState = rememberSwipeToDismissBoxState()
        val verticalOffset = remember { Animatable(0f) }
        val screenHeight = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

        LaunchedEffect(dismissState) {
            snapshotFlow { dismissState.currentValue }
                .filter { it != SwipeToDismissBoxValue.Settled }
                .distinctUntilChanged()
                .collect {
                    selectedDay = null
                }
        }

        LaunchedEffect(selectedDay) {
            if (selectedDay != null) {
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                verticalOffset.snapTo(0f)
            }
        }

        AnimatedVisibility(
            visible = selectedDay != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            selectedDay?.let { dayRes ->
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = true,
                    backgroundContent = {},
                    content = {
                        DaySummaryCard(
                            day = dayRes.day,
                            onClick = { onDayClick(dayRes.id) },
                            onDismiss = { selectedDay = null },
                            modifier = Modifier
                                .padding(16.dp)
                                .offset { IntOffset(0, verticalOffset.value.roundToInt()) }
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures(
                                        onVerticalDrag = { _, dragAmount ->
                                            scope.launch {
                                                verticalOffset.snapTo(verticalOffset.value + dragAmount)
                                            }
                                        },
                                        onDragEnd = {
                                            if (verticalOffset.value > 150) {
                                                scope.launch {
                                                    verticalOffset.animateTo(screenHeight, tween(300))
                                                    selectedDay = null
                                                }
                                            } else {
                                                scope.launch {
                                                    verticalOffset.animateTo(0f, tween(300))
                                                }
                                            }
                                        }
                                    )
                                }
                        )
                    }
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
        color = colorScheme.onSurface,
    )
}

@Composable
private fun MonthEventSummary(
    summaries: Map<Int, List<MonthSummary>>,
    month: CalendarMonth
) {
    val summary = summaries[month.yearMonth.year]?.find { it.month == month.yearMonth.monthValue }
    if (summary == null || (
                summary.highlightedDays.isEmpty()
                        && summary.highlightedPeriods.isEmpty())
    ) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "События месяца",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = colorScheme.primary,
            )

            summary.highlightedPeriods.forEach { period ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Text(
                        text = "${period.startDay} - ${period.endDay}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(56.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ColorFast), // Assuming periods are always fasts
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = period.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            summary.highlightedDays.forEach { day ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { /* Add navigation later */ }
                        .padding(vertical = 4.dp),
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(56.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ColorFeast), // Using a default feast color since LiturgicalColor is gone
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = day.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}


@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    dayResource: CalendarDayResource?,
    onClick: (CalendarDayResource?) -> Unit,
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val isToday = day.date == today && isCurrentMonth

    val style = resolveDayStyle(day, dayResource, isCurrentMonth, isToday)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(style.backgroundColor)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(
                enabled = isCurrentMonth,
                onClick = { onClick(dayResource) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = style.textColor,
                fontWeight = style.fontWeight,
            )

            // Полоски-индикаторы под числом.
            // Многодневный пост обозначен фоном — полоска ему не нужна.
            // Полоски = акцентные события: праздники, поминовения, однодневный пост.
            val indicators = remember(dayResource) {
                if (!isCurrentMonth || dayResource == null) {
                    return@remember emptyList()
                }

                val list = mutableListOf<Color>()
                val localDayTypes = dayResource.day.dayTypes
                val isLongFast = localDayTypes.contains(DayType.LONG_FAST)
                val isSingleDayFast =
                    !isLongFast && (
                            dayResource.day.fastingInfo.fastingLevel != FastingLevel.NONE ||
                                    localDayTypes.contains(DayType.FAST)
                            )

                // 1. Праздник
                if (localDayTypes.any { dayType -> dayType.isFeast() }) {
                    list.add(ColorFeast)
                }

                // 2. Поминовение
                if (localDayTypes.contains(DayType.COMMEMORATION)) {
                    list.add(ColorMemorial)
                }

                // 3. Однодневный пост (многодневный уже показан фоном)
                if (isSingleDayFast) {
                    list.add(ColorFast)
                }

                return@remember list
            }

            if (indicators.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        2.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    indicators.take(3).forEach { color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(color, RoundedCornerShape(2.dp)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDaysOfWeekHeader(daysOfWeek: List<DayOfWeek>) {
    Surface(color = colorScheme.background) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
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
            monthUiState = MonthScreenUiState.Success(
                days = emptyList(),
                summaries = emptyMap()
            ),
            onDayClick = {},
            scrollToToday = emptyFlow(),
            onVisibleYearChanged = {},
        )
    }
}

@Preview(showSystemUi = true, name = "Month Screen Full")
@Composable
private fun MonthScreenFullPreview() {
    val today = LocalDate.now()
    val sampleDays = listOf(
        CalendarDayResource(
            id = "1",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 1,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 12,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.FEAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 3
                ),
                fastingInfo = FastingInfo(fastingLevel = FastingLevel.NONE, allowed = emptyList()),
                title = "Великий праздник",
                dayOfWeek = kotlinx.datetime.DayOfWeek.MONDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "2",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 2,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 11,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.LONG_FAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 0
                ),
                fastingInfo = FastingInfo(
                    fastingLevel = FastingLevel.STRICT,
                    allowed = emptyList(),
                    fastingName = "Петров пост"
                ),
                title = "Начало Петрова поста",
                dayOfWeek = kotlinx.datetime.DayOfWeek.TUESDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "3",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 3,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 10,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.LONG_FAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 0
                ),
                fastingInfo = FastingInfo(
                    fastingLevel = FastingLevel.STRICT,
                    allowed = emptyList(),
                    fastingName = "Петров пост"
                ),
                title = "Петров пост",
                dayOfWeek = kotlinx.datetime.DayOfWeek.WEDNESDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "5",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 5,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 8,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.COMMEMORATION),
                liturgicalInfo = LiturgicalInfo(
                    importance = 2
                ),
                fastingInfo = FastingInfo(fastingLevel = FastingLevel.NONE, allowed = emptyList()),
                title = "Поминовение усопших",
                dayOfWeek = kotlinx.datetime.DayOfWeek.FRIDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        )
    )

    val useCase = GenerateMonthSummariesUseCase()
    val summaries = useCase(sampleDays, today.year)
    val summariesMap = mapOf(today.year to summaries)


    CalendarTheme {
        MonthScreen(
            monthUiState = MonthScreenUiState.Success(
                days = sampleDays,
                summaries = summariesMap
            ),
            onDayClick = {},
            scrollToToday = emptyFlow(),
            onVisibleYearChanged = {},
        )
    }
}

@Preview(showSystemUi = true, name = "Month Screen With Day Card")
@Composable
private fun MonthScreenWithDayCardPreview() {
    val today = LocalDate.now()
    val sampleDays = listOf(
        CalendarDayResource(
            id = "1",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 1,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 12,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.FEAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 3
                ),
                fastingInfo = FastingInfo(fastingLevel = FastingLevel.NONE, allowed = emptyList()),
                title = "Великий праздник",
                dayOfWeek = kotlinx.datetime.DayOfWeek.MONDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "2",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 2,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 11,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.LONG_FAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 0
                ),
                fastingInfo = FastingInfo(
                    fastingLevel = FastingLevel.STRICT,
                    allowed = emptyList(),
                    fastingName = "Петров пост"
                ),
                title = "Начало Петрова поста",
                dayOfWeek = kotlinx.datetime.DayOfWeek.TUESDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "3",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 3,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 10,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.LONG_FAST),
                liturgicalInfo = LiturgicalInfo(
                    importance = 0
                ),
                fastingInfo = FastingInfo(
                    fastingLevel = FastingLevel.STRICT,
                    allowed = emptyList(),
                    fastingName = "Петров пост"
                ),
                title = "Петров пост",
                dayOfWeek = kotlinx.datetime.DayOfWeek.WEDNESDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        ),
        CalendarDayResource(
            id = "5",
            day = ru.akhilko.christian_calendar.core.model.CalendarDay(
                gregorianDay = today.dayOfMonth + 5,
                gregorianMonth = today.monthValue,
                gregorianYear = today.year,
                julianDay = today.dayOfMonth - 8,
                julianMonth = today.monthValue,
                julianYear = today.year,
                dayTypes = listOf(DayType.COMMEMORATION),
                liturgicalInfo = LiturgicalInfo(
                    importance = 2
                ),
                fastingInfo = FastingInfo(fastingLevel = FastingLevel.NONE, allowed = emptyList()),
                title = "Поминовение усопших",
                dayOfWeek = kotlinx.datetime.DayOfWeek.FRIDAY,
                lastUpdated = "",
                week = "",
                readings = emptyList(),
                saints = emptyList(),
                searchText = ""
            )
        )
    )

    val useCase = GenerateMonthSummariesUseCase()
    val summaries = useCase(sampleDays, today.year)
    val summariesMap = mapOf(today.year to summaries)


    CalendarTheme {
        MonthScreen(
            monthUiState = MonthScreenUiState.Success(
                days = sampleDays,
                summaries = summariesMap
            ),
            onDayClick = {},
            scrollToToday = emptyFlow(),
            onVisibleYearChanged = {},
            initialSelectedDay = sampleDays.first()
        )
    }
}
