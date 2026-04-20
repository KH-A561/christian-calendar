package ru.akhilko.christian_calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import androidx.tracing.trace
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.akhilko.christian_calendar.navigation.TopLevelDestination
import ru.akhilko.christian_calendar.navigation.TopLevelDestination.DAY
import ru.akhilko.christian_calendar.navigation.TopLevelDestination.MONTH
import ru.akhilko.christian_calendar.navigation.TopLevelDestination.WEEK
import ru.akhilko.core.ui.state.SelectedDayHolder
import ru.akhilko.day.navigation.DAY_ROUTE_BASE
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.feature.search.navigation.navigateToSearch
import ru.akhilko.month.navigation.MONTH_ROUTE
import ru.akhilko.month.navigation.navigateToMonth
import ru.akhilko.week.navigation.WEEK_ROUTE
import ru.akhilko.week.navigation.navigateToWeek

@Stable
class ChristianCalendarAppState(
    val navController: NavHostController,
    val selectedDayHolder: SelectedDayHolder,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when {
            currentDestination?.route?.contains(MONTH_ROUTE, ignoreCase = true) == true -> MONTH
            currentDestination?.route?.contains(WEEK_ROUTE, ignoreCase = true) == true -> WEEK
            currentDestination?.route?.contains(DAY_ROUTE_BASE, ignoreCase = true) == true -> DAY
            else -> null
        }

    private val _scrollToTodayRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToTodayRequests = _scrollToTodayRequests.asSharedFlow()

    fun scrollToToday() {
        _scrollToTodayRequests.tryEmit(Unit)
    }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            when (topLevelDestination) {
                MONTH -> navController.navigateToMonth(topLevelNavOptions)
                WEEK -> navController.navigateToWeek(topLevelNavOptions)
                DAY -> {
                    // Открываем последний просмотренный день, а не «сегодня».
                    val targetId = selectedDayHolder.selectedDayId.value
                    navController.navigateToDay(targetId, topLevelNavOptions)
                }
            }
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
}
