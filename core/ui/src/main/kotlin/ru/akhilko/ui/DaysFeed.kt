package ru.akhilko.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.akhilko.christian_calendar.core.data.model.DayResource
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
                key = { it.day.id },
                contentType = { "daysFeedItem" },
            ) { dayResource ->
                val context = LocalContext.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

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
        val feed: List<DayResource>,
    ) : DaysFeedUiState
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