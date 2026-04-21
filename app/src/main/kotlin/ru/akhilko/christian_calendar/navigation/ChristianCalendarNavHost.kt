package ru.akhilko.christian_calendar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import ru.akhilko.christian_calendar.ui.ChristianCalendarAppState
import ru.akhilko.day.navigation.DAY_ROUTE
import ru.akhilko.day.navigation.dayScreen
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.month.navigation.MONTH_GRAPH_ROUTE
import ru.akhilko.feature.search.navigation.searchScreen
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
    startDestination: String = MONTH_GRAPH_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        monthScreen(
            // Month → Day: обычный переход, Back возвращает на Month.
            onDayClick = { dayId -> navController.navigateToDay(dayId) },
        )
        weekScreen(
            onDayClick = { dayId ->
                val navOptions = navOptions {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                navController.navigateToDay(dayId, navOptions)
            },
            onShowSnackbar = onShowSnackbar,
        )
        dayScreen(
            onBack = navController::popBackStack,
            // Day → Day (prev/next / FAB «К сегодня»): заменяем текущую Day-запись,
            // чтобы Back возвращал туда, откуда пришли, а не листал по дням.
            onNavigateToDay = { dayId ->
                val navOptions = navOptions {
                    popUpTo(DAY_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToDay(dayId, navOptions)
            },
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onBackClick = navController::popBackStack,
        )
    }
}
