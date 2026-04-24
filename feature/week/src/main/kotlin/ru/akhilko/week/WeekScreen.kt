package ru.akhilko.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.week.ui.WeekDayRow
import ru.akhilko.week.ui.WeekTopBar
import ru.akhilko.week.ui.formatWeekRange
import java.time.LocalDate

@Composable
fun WeekScreen(
    uiState: WeekUiState,
    onNavigateToSearch: () -> Unit,
    onMenuClick: () -> Unit,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDayClick: (String) -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        WeekUiState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        WeekUiState.Error -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Не удалось загрузить неделю",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        is WeekUiState.Success -> {
            Scaffold(
                topBar = {
                    WeekTopBar(
                        dateRange = formatWeekRange(uiState.weekStart),
                        sedmicaText = uiState.days.firstOrNull()?.weekText,
                        onMenuClick = onMenuClick,
                        onPrevWeek = onPrevWeek,
                        onNextWeek = onNextWeek,
                        onSearchClick = onNavigateToSearch,
                    )
                },
                floatingActionButton = floatingActionButton,
                modifier = modifier,
                containerColor = Color.Transparent,
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.days, key = { it.id }) { day ->
                        WeekDayRow(
                            day = day,
                            onDayClick = onDayClick,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "WeekScreen - List")
@Composable
private fun WeekScreenListPreview() {
    CalendarTheme {
        Surface {
            WeekScreen(
                uiState = WeekUiState.Success(
                    weekStart = LocalDate.of(2026, 4, 20),
                    days = previewDays(),
                ),
                onNavigateToSearch = {},
                onMenuClick = {},
                onPrevWeek = {},
                onNextWeek = {},
                onDayClick = {},
            )
        }
    }
}

@Preview(name = "WeekScreen - Loading")
@Composable
private fun WeekScreenLoadingPreview() {
    CalendarTheme {
        Surface {
            WeekScreen(
                uiState = WeekUiState.Loading,
                onNavigateToSearch = {},
                onMenuClick = {},
                onPrevWeek = {},
                onNextWeek = {},
                onDayClick = {},
            )
        }
    }
}

@Preview(name = "WeekScreen - Error")
@Composable
private fun WeekScreenErrorPreview() {
    CalendarTheme {
        Surface {
            WeekScreen(
                uiState = WeekUiState.Error,
                onNavigateToSearch = {},
                onMenuClick = {},
                onPrevWeek = {},
                onNextWeek = {},
                onDayClick = {},
            )
        }
    }
}

private fun previewDays(): List<CalendarDayPresentation> = listOf(
    previewDay(
        id = "2026-04-20",
        dayNum = 20,
        weekdayRu = "Понедельник",
        weekdayShortRu = "Пн",
        title = "Собор преподобных отцов",
        isToday = true,
        badges = listOf(BadgeKind.GREAT, BadgeKind.FAST),
    ),
    previewDay(
        id = "2026-04-21",
        dayNum = 21,
        weekdayRu = "Вторник",
        weekdayShortRu = "Вт",
        title = "Память великомученика",
        isToday = false,
        badges = listOf(BadgeKind.REMEMBRANCE),
    ),
    previewDay(
        id = "2026-04-22",
        dayNum = 22,
        weekdayRu = "Среда",
        weekdayShortRu = "Ср",
        title = "Обычный день",
        isToday = false,
        badges = emptyList(),
    ),
)

private fun previewDay(
    id: String,
    dayNum: Int,
    weekdayRu: String,
    weekdayShortRu: String,
    title: String,
    isToday: Boolean,
    badges: List<BadgeKind>,
): CalendarDayPresentation = CalendarDayPresentation(
    id = id,
    gregorianDayNum = dayNum,
    gregorianMonth = 4,
    gregorianYear = 2026,
    julianDay = dayNum - 13,
    julianMonth = 4,
    julianYear = 2026,
    monthYearRu = "Апрель 2026",
    weekdayRu = weekdayRu,
    weekdayShortRu = weekdayShortRu,
    badges = badges,
    title = title,
    weekText = "Светлая седмица",
    fastingLevelRu = "Поста нет",
    fastingName = "Петров пост",
    allowed = listOf("Рыба"),
    readings = listOf("Ин. 10:1-9", "Евр. 7:26-8:2"),
    saints = listOf("Прп. Сергий Радонежский", "Свт. Николай Чудотворец"),
    isToday = isToday,
)
