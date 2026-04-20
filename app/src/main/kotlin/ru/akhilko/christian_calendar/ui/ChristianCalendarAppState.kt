package ru.akhilko.christian_calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
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
import ru.akhilko.day.navigation.DAY_GRAPH_ROUTE
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.feature.search.navigation.navigateToSearch
import ru.akhilko.month.navigation.MONTH_GRAPH_ROUTE
import ru.akhilko.month.navigation.navigateToMonth
import ru.akhilko.week.navigation.WEEK_GRAPH_ROUTE
import ru.akhilko.week.navigation.navigateToWeek

@Stable
class ChristianCalendarAppState(
    val navController: NavHostController,
    private val selectedDayHolder: SelectedDayHolder,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            val destination = currentDestination
            return topLevelDestinations.firstOrNull { topLevelDestination ->
                destination?.hierarchy?.any { it.route == topLevelDestination.route } == true
            }
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
                MONTH -> navigateToRestoredTopLevel(MONTH_GRAPH_ROUTE) {
                    navController.navigateToMonth(topLevelNavOptions)
                }

                WEEK -> navigateToRestoredTopLevel(WEEK_GRAPH_ROUTE) {
                    navController.navigateToWeek(topLevelNavOptions)
                }

                DAY -> {
                    // Открываем последний просмотренный день, а не «сегодня».
                    val targetId = selectedDayHolder.selectedDayId.value
                    navigateToRestoredTopLevel(DAY_GRAPH_ROUTE) {
                        navController.navigateToDay(targetId, topLevelNavOptions)
                    }
                }
            }
        }
    }

    private fun navigateToRestoredTopLevel(
        route: String,
        fallbackNavigate: () -> Unit,
    ) {
        val restored = navController.popBackStack(route, inclusive = false)
        if (!restored) {
            fallbackNavigate()
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
}
