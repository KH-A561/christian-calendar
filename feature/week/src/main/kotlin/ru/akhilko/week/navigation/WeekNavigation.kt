package ru.akhilko.week.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import ru.akhilko.week.WeekRoute

const val WEEK_GRAPH_ROUTE = "week_graph"
const val WEEK_ROUTE = "week_route"

fun NavController.navigateToWeek(navOptions: NavOptions) = navigate(WEEK_GRAPH_ROUTE, navOptions)

fun NavGraphBuilder.weekScreen(
    onDayClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onNavigateToSearch: () -> Unit,
    onMenuClick: () -> Unit,
) {
    navigation(
        route = WEEK_GRAPH_ROUTE,
        startDestination = WEEK_ROUTE,
    ) {
        composable(route = WEEK_ROUTE) {
            WeekRoute(
                onDayClick = onDayClick,
                onNavigateToSearch = onNavigateToSearch,
                onMenuClick = onMenuClick,
            )
        }
    }
}