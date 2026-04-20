package ru.akhilko.week

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import ru.akhilko.week.ui.WeekHeader
import ru.akhilko.week.ui.WeekList
import ru.akhilko.week.ui.search.SearchModeBar
import ru.akhilko.week.ui.search.SearchResultsList

@Composable
fun WeekScreen(
    uiState: WeekUiState,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToggleMode: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onFilterSelected: (ru.akhilko.core.designsystem.component.BadgeKind?) -> Unit,
    onResetToCurrentWeek: () -> Unit,
    onDayClick: (String) -> Unit
) {
    when (uiState) {
        WeekUiState.Loading -> CenteredLoading()
        WeekUiState.Error -> CenteredError("Не удалось загрузить неделю")
        is WeekUiState.Success -> WeekSuccessContent(
            state = uiState,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek,
            onToggleMode = onToggleMode,
            onQueryChanged = onQueryChanged,
            onFilterSelected = onFilterSelected,
            onResetToCurrentWeek = onResetToCurrentWeek,
            onDayClick = onDayClick
        )
    }
}

@Composable
private fun WeekSuccessContent(
    state: WeekUiState.Success,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToggleMode: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onFilterSelected: (ru.akhilko.core.designsystem.component.BadgeKind?) -> Unit,
    onResetToCurrentWeek: () -> Unit,
    onDayClick: (String) -> Unit
) {
    val currentIsoWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val isNotCurrentWeek = state.weekStart != currentIsoWeekStart

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isNotCurrentWeek,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = onResetToCurrentWeek,
                    icon = { Icon(Icons.Default.Today, contentDescription = null) },
                    text = { Text("К сегодня") }
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            WeekHeader(
                weekStart = state.weekStart,
                mode = state.mode,
                onPrevWeek = onPrevWeek,
                onNextWeek = onNextWeek,
                onToggleMode = onToggleMode
            )

            if (state.mode == WeekMode.SEARCH) {
                SearchModeBar(
                    query = state.query,
                    onQueryChanged = onQueryChanged,
                    selectedFilter = state.filter,
                    onFilterSelected = onFilterSelected
                )

                SearchResultsList(
                    results = state.results,
                    onDayClick = onDayClick
                )
            } else {
                WeekList(
                    days = state.days,
                    onDayClick = onDayClick
                )
            }
        }
    }
}

@Composable
private fun CenteredLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredError(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message)
    }
}