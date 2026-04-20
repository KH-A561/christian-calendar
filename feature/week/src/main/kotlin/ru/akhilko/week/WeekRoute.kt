package ru.akhilko.week

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ru.akhilko.core.ui.state.SelectedDayHolder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun WeekRoute(
    onDayClick: (String) -> Unit,
    viewModel: WeekViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectedDayHolder = remember(context) { context.selectedDayHolder() }

    Scaffold(
        floatingActionButton = {
            val success = uiState as? WeekUiState.Success
            val isCurrentWeek = success?.weekStart == currentWeekStart()
            AnimatedVisibility(
                visible = success != null && !isCurrentWeek,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = viewModel::resetToCurrentWeek,
                    icon = { Icon(imageVector = Icons.Default.Today, contentDescription = null) },
                    text = { Text("К сегодня") },
                )
            }
        },
        containerColor = Color.Transparent,
    ) { padding ->
        WeekScreen(
            uiState = uiState,
            onPrevWeek = viewModel::prevWeek,
            onNextWeek = viewModel::nextWeek,
            onToggleMode = viewModel::toggleMode,
            onQueryChanged = viewModel::onQueryChanged,
            onFilterSelected = viewModel::onFilterSelected,
            onDayClick = { id ->
                selectedDayHolder.setSelectedDay(id)
                onDayClick(id)
            },
            modifier = Modifier.padding(padding),
        )
    }
}

private fun currentWeekStart(): LocalDate =
    LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

private fun Context.selectedDayHolder(): SelectedDayHolder {
    val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        WeekSelectedDayEntryPoint::class.java,
    )
    return entryPoint.selectedDayHolder()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WeekSelectedDayEntryPoint {
    fun selectedDayHolder(): SelectedDayHolder
}
