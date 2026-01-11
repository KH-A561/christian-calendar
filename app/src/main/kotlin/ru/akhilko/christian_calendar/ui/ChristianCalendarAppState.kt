package ru.akhilko.christian_calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import ru.akhilko.day.navigation.DAY_ROUTE
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.feature.search.navigation.navigateToSearch
import ru.akhilko.month.navigation.MONTH_ROUTE
import ru.akhilko.month.navigation.navigateToMonth
import ru.akhilko.week.navigation.WEEK_ROUTE
import ru.akhilko.week.navigation.navigateToWeek
import java.time.LocalDate

@Stable
class ChristianCalendarAppState(
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            MONTH_ROUTE -> MONTH
            WEEK_ROUTE -> WEEK
            DAY_ROUTE -> DAY
            else -> null
        }

    // Динамический заголовок для TopAppBar
    var topAppBarTitle by mutableStateOf("")

    // Событие для прокрутки к сегодняшнему дню
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
                    val today = LocalDate.now().toString()
                    navController.navigateToDay(today, topLevelNavOptions)
                }
            }
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
}
