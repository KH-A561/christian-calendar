package ru.akhilko.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo
import ru.akhilko.core.designsystem.theme.CalendarTheme

@OptIn(ExperimentalFoundationApi::class)
fun LazyStaggeredGridScope.daysFeed(
    feedState: DaysFeedUiState,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        DaysFeedUiState.Loading -> Unit
        is DaysFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "daysFeedItem" },
            ) { dayResource ->
                DayResourceCardExpanded(
                    day = dayResource.day,
                    onClick = {
                        onExpandedCardClick()
//                        analyticsHelper.logNewsResourceOpened(
//                            newsResourceId = daysResource.id,
//                        )
//                      todo: открытие дня на отдельном экране Дня
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItemPlacement(),
                )
            }
        }
    }
}

sealed interface DaysFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : DaysFeedUiState

    data class Success(
        val feed: List<CalendarDayResource>,
    ) : DaysFeedUiState
}


@Preview
@Composable
private fun DaysFeedSuccessPreview() {
    val sampleDay = CalendarDay(
        dayOfWeek = DayOfWeek.WEDNESDAY,
        gregorianDay = 12,
        gregorianMonth = 2,
        gregorianYear = 2025,
        lastUpdated = "21 August 2025 at 17:53:08 UTC+3",
        title = "Предпразднство Преображения Господня",
        week = "Седмица 11-я по Пятидесятнице.",
        liturgicalInfo = LiturgicalInfo(
            color = LiturgicalColor.PURPLE,
            dayType = DayType.FOREFEAST,
            importance = 3
        ),
        fastingInfo = FastingInfo(
            fastingLevel = FastingLevel.PARTIAL,
            allowed = listOf("Вино", "Елей")
        ),
        readings = emptyList(),
        saints = emptyList(),
        searchText = ""
    )
    val sampleResource = CalendarDayResource(
        id = sampleDay.id,
        day = sampleDay,
        holidays = emptyList(),
        fastingInformation = sampleDay.fastingInfo
    )

    CalendarTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            daysFeed(
                feedState = DaysFeedUiState.Success(
                    listOf(sampleResource)
                )
            )
        }
    }
}

@Preview
@Composable
private fun DaysFeedLoadingPreview() {
    CalendarTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            daysFeed(
                feedState = DaysFeedUiState.Loading
            )
        }
    }
}