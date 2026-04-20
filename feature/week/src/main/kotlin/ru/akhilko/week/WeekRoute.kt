package ru.akhilko.week

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.akhilko.core.ui.state.SelectedDayHolder

@Composable
fun WeekRoute(
    onDayClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: WeekViewModel = hiltViewModel(),
    selectedDayHolder: SelectedDayHolder,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeekScreen(
        uiState = uiState,
        onPrevWeek = viewModel::prevWeek,
        onNextWeek = viewModel::nextWeek,
        onToggleMode = viewModel::toggleMode,
        onQueryChanged = viewModel::onQueryChanged,
        onFilterSelected = viewModel::onFilterSelected,
        onResetToCurrentWeek = viewModel::resetToCurrentWeek,
        onDayClick = { id ->
            selectedDayHolder.setSelectedDay(id)
            onDayClick(id)
        }
    )
}
