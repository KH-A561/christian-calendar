package ru.akhilko.christian_calendar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.akhilko.christian_calendar.ui.ChristianCalendarAppState
import ru.akhilko.day.navigation.dayScreen
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.feature.search.navigation.searchScreen
import ru.akhilko.month.MonthViewModel
import ru.akhilko.month.navigation.MONTH_ROUTE
import ru.akhilko.month.navigation.monthScreen
import ru.akhilko.week.navigation.weekScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun ChristianCalendarNavHost(
    appState: ChristianCalendarAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = MONTH_ROUTE,
    monthViewModel: MonthViewModel
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        monthScreen(
            onDayClick = { dayId ->
                navController.navigateToDay(dayId)
            },
            viewModel = monthViewModel
        )
        weekScreen(
            onShowSnackbar = onShowSnackbar,
        )
        dayScreen(
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onBackClick = navController::popBackStack,
        )
    }
}
