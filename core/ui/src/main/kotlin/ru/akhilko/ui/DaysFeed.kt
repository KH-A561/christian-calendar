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
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.core.designsystem.theme.CalendarTheme
import java.util.UUID
import kotlin.time.Duration

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
                    day = dayResource.day(),
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
    CalendarTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            daysFeed(
                feedState = DaysFeedUiState.Success(
                    listOf(
                        CalendarDayResource(
                            UUID.randomUUID().toString(),
                            DayOfWeek.WEDNESDAY,
                            Clock.System.now(),
                            Clock.System.now().plus(
                                Duration.Companion.parse("1d")
                            ),
                            title = "Title",
                            weekInfo = "Week Info",
                            primarySaints = emptyList(),
                            secondarySaints = emptyList(),
                            readings = emptyMap(),
                            tags = emptyList(),
                            fasting = null
                        )
                    )
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