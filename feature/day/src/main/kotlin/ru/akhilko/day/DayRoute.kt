package ru.akhilko.day

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DayRoute(
    onBack: () -> Unit,
    onNavigateToDay: (String) -> Unit,
    viewModel: DayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DayScreen(
        uiState = uiState,
        onBack = onBack,
        onNavigateToDay = onNavigateToDay,
    )
}
