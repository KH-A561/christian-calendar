package ru.akhilko.month

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
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
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.designsystem.theme.ColorEaster
import ru.akhilko.core.designsystem.theme.ColorEasterSurface
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorGreat
import ru.akhilko.core.designsystem.theme.ColorGreatFeast
import ru.akhilko.core.designsystem.theme.ColorRemembrance
import ru.akhilko.core.designsystem.theme.ColorTwelveFeast
import ru.akhilko.core.ui.format.monthNominativeRu
import ru.akhilko.month.ui.MonthEventSummary
import ru.akhilko.month.ui.MonthTopBar
import ru.akhilko.ui.DaySummaryCard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import kotlin.math.roundToInt
import java.util.Locale as JavaLocale

private data class DayStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val fontWeight: FontWeight,
    val isFast: Boolean,
    val borderColor: Color? = null,
)

@Composable
private fun resolveDayStyle(
    day: CalendarDay,
    dayResource: CalendarDayResource?,
    isCurrentMonth: Boolean,
    isToday: Boolean,
): DayStyle {
    val dayData = dayResource?.day
    val dayTypes = dayData?.dayTypes ?: emptyList()

    val isEaster = dayTypes.contains(DayType.EASTER)
    val isTwelve = dayTypes.contains(DayType.TWELVE_GREAT_FEASTS)
    val isGreat = dayTypes.contains(DayType.GREAT_FEAST)
    val isCommemoration = dayTypes.contains(DayType.COMMEMORATION)
    val isSunday = day.date.dayOfWeek == DayOfWeek.SUNDAY
    val isFast = dayTypes.contains(DayType.LONG_FAST) ||
        dayTypes.contains(DayType.FAST) ||
        dayData?.fastingInfo?.fastingLevel != FastingLevel.NONE

    val backgroundColor = when {
        !isCurrentMonth -> Color.Transparent
        isEaster -> ColorEasterSurface
        isTwelve -> ColorTwelveFeast.copy(alpha = 0.55f)        // насыщенный бордо
        isGreat -> ColorGreatFeast.copy(alpha = 0.30f)          // мягкий розовый
        isCommemoration -> ColorRemembrance.copy(alpha = 0.55f)
        isSunday -> Color.Transparent
        else -> Color.Transparent
    }

    val textColor = when {
        !isCurrentMonth -> colorScheme.onSurface.copy(alpha = 0.25f)
        isEaster -> ColorEaster
        isTwelve -> Color.White                                 // белый на тинте бордо
        isGreat -> ColorTwelveFeast                             // тёмный бордо на розовом
        isSunday -> ColorGreatFeast
        else -> colorScheme.onSurface
    }

    val fontWeight = when {
        isToday -> FontWeight.Black
        isTwelve -> FontWeight.Bold
        isEaster || isGreat -> FontWeight.SemiBold
        isSunday -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }

    val borderColor = if (isEaster && isCurrentMonth) ColorEaster else null
    return DayStyle(backgroundColor, textColor, fontWeight, isFast, borderColor)
}

@Composable
fun MonthRoute(
    modifier: Modifier = Modifier,
    onDayClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: MonthViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    val monthUiState: MonthScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    MonthScreen(
        monthUiState = monthUiState,
        onDayClick = onDayClick,
        onNavigateToSearch = onNavigateToSearch,
        onMenuClick = onMenuClick,
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
    onNavigateToSearch: () -> Unit,
    onMenuClick: () -> Unit,
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
                val dayResourcesByDate = remember(days) {
                    days.associateBy { it.day.getGregorianLocalDate() }
                }
                Scaffold(
                    topBar = {
                        MonthTopBar(
                            visibleMonth = visibleMonth,
                            onPrevMonth = {
                                scope.launch {
                                    state.animateScrollToMonth(visibleMonth.minusMonths(1))
                                }
                            },
                            onNextMonth = {
                                scope.launch {
                                    state.animateScrollToMonth(visibleMonth.plusMonths(1))
                                }
                            },
                            onSearchClick = onNavigateToSearch,
                            onMenuClick = onMenuClick,
                        )
                    },
                    containerColor = Color.Transparent,
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        MonthDaysOfWeekHeader(daysOfWeek)
                        VerticalCalendar(
                                state = state,
                                userScrollEnabled = true,
                                calendarScrollPaged = false,
                                dayContent = { day ->
                                    val dayResource = dayResourcesByDate[day.date.toKotlinLocalDate()]
                                    Day(
                                        day = day,
                                        today = today,
                                        dayResource = dayResource,
                                        isSelected = selectedDay?.id == dayResource?.id,
                                        onClick = { dayRes ->
                                            selectedDay = if (selectedDay == dayRes) null else dayRes
                                        },
                                    )
                                },
                                monthHeader = { month ->
                                    MonthSectionHeader(
                                        yearMonth = month.yearMonth,
                                    )
                                },
                                monthContainer = { _, container ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                    ) {
                                        container()
                                        HorizontalDivider(
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 16.dp,
                                            ),
                                            thickness = 1.dp,
                                            color = colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        )
                                    }
                                },
                                monthFooter = { month ->
                                    Column {
                                        Spacer(Modifier.height(8.dp))
                                        MonthEventSummary(
                                            summaries = monthUiState.summaries,
                                            month = month,
                                            dayResources = monthUiState.days,
                                            onEventClick = onDayClick,
                                        )
                                        Spacer(Modifier.height(16.dp))
                                    }
                                },
                            )
                    }
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
private fun MonthSectionHeader(yearMonth: YearMonth) {
    Text(
        text = "${monthNominativeRu(yearMonth.monthValue)} ${yearMonth.year}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    dayResource: CalendarDayResource?,
    isSelected: Boolean,
    onClick: (CalendarDayResource?) -> Unit,
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val isToday = day.date == today && isCurrentMonth

    val style = resolveDayStyle(day, dayResource, isCurrentMonth, isToday)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(style.backgroundColor)
            .then(
                when {
                    isToday -> Modifier.border(
                        width = 1.5.dp,
                        color = colorScheme.onSurface,
                        shape = RoundedCornerShape(8.dp),
                    )
                    style.borderColor != null -> Modifier.border(
                        width = 1.dp,
                        color = style.borderColor,
                        shape = RoundedCornerShape(8.dp),
                    )
                    isSelected -> Modifier.border(
                        width = 1.dp,
                        color = ColorGreat,
                        shape = RoundedCornerShape(8.dp),
                    )
                    else -> Modifier
                }
            )
            .clickable(
                enabled = isCurrentMonth,
                onClick = { onClick(dayResource) },
            ),
    ) {
        if (style.isFast && isCurrentMonth) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(ColorFast),
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = style.textColor,
                fontWeight = style.fontWeight,
            )
            val julianDay = dayResource?.day?.julianDay
            if (isCurrentMonth && julianDay != null) {
                Text(
                    text = julianDay.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
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
            onNavigateToSearch = {},
            onMenuClick = {},
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
            onNavigateToSearch = {},
            onMenuClick = {},
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
            onNavigateToSearch = {},
            onMenuClick = {},
            scrollToToday = emptyFlow(),
            onVisibleYearChanged = {},
            initialSelectedDay = sampleDays.first()
        )
    }
}
