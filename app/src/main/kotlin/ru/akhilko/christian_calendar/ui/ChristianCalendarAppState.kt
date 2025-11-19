package ru.akhilko.christian_calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.DAY
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.MONTH
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.WEEK
import ru.akhilko.day.navigation.DAY_ROUTE
import ru.akhilko.day.navigation.navigateToDay
import ru.akhilko.feature.search.navigation.navigateToSearch
import ru.akhilko.month.navigation.MONTH_ROUTE
import ru.akhilko.month.navigation.navigateToMonth
import ru.akhilko.week.navigation.WEEK_ROUTE
import ru.akhilko.week.navigation.navigateToWeek

@Stable
class ChristianCalendarAppState(
    val navController: NavHostController,
    monthRepository: MonthRepository,
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

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                MONTH -> navController.navigateToMonth(topLevelNavOptions)
                WEEK -> navController.navigateToWeek(topLevelNavOptions)
                DAY -> navController.navigateToDay(topLevelNavOptions)
            }
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
}