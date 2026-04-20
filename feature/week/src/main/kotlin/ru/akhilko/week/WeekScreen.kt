package ru.akhilko.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.week.ui.WeekHeader
import ru.akhilko.week.ui.WeekList
import ru.akhilko.week.ui.search.SearchModeBar
import ru.akhilko.week.ui.search.SearchResultsList
import java.time.LocalDate

@Composable
fun WeekScreen(
    uiState: WeekUiState,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToggleMode: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onFilterSelected: (BadgeKind?) -> Unit,
    onDayClick: (String) -> Unit,
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
            Column(modifier = modifier.fillMaxSize()) {
                WeekHeader(
                    weekStart = uiState.weekStart,
                    mode = uiState.mode,
                    onPrevWeek = onPrevWeek,
                    onNextWeek = onNextWeek,
                    onToggleMode = onToggleMode,
                )

                if (uiState.mode == WeekMode.SEARCH) {
                    SearchModeBar(
                        query = uiState.query,
                        selectedFilter = uiState.filter,
                        onQueryChanged = onQueryChanged,
                        onFilterSelected = onFilterSelected,
                    )
                    SearchResultsList(
                        results = uiState.results,
                        query = uiState.query,
                        onDayClick = onDayClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    WeekList(
                        days = uiState.days,
                        onDayClick = onDayClick,
                        modifier = Modifier.fillMaxSize(),
                    )
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
                    mode = WeekMode.LIST,
                    query = "",
                    filter = null,
                    results = emptyList(),
                ),
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
                onQueryChanged = {},
                onFilterSelected = {},
                onDayClick = {},
            )
        }
    }
}

@Preview(name = "WeekScreen - Search")
@Composable
private fun WeekScreenSearchPreview() {
    CalendarTheme {
        Surface {
            WeekScreen(
                uiState = WeekUiState.Success(
                    weekStart = LocalDate.of(2026, 4, 20),
                    days = previewDays(),
                    mode = WeekMode.SEARCH,
                    query = "Сергий",
                    filter = BadgeKind.GREAT,
                    results = previewDays().take(2),
                ),
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
                onQueryChanged = {},
                onFilterSelected = {},
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
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
                onQueryChanged = {},
                onFilterSelected = {},
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
                onPrevWeek = {},
                onNextWeek = {},
                onToggleMode = {},
                onQueryChanged = {},
                onFilterSelected = {},
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
