package ru.akhilko.month.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import ru.akhilko.month.MonthRoute

const val CENTERED_MONTH = "centeredMonth"
const val CENTERED_YEAR = "centeredYear"
const val DAY_SELECTED = "daySelected"
const val MONTH_GRAPH_ROUTE = "month_graph"
const val MONTH_ROUTE = "month_route"

fun NavController.navigateToMonth(navOptions: NavOptions) = navigate(MONTH_GRAPH_ROUTE, navOptions)

fun NavGraphBuilder.monthScreen(
    onDayClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    navigation(
        route = MONTH_GRAPH_ROUTE,
        startDestination = MONTH_ROUTE,
    ) {
        composable(route = MONTH_ROUTE) {
            MonthRoute(
                onDayClick = onDayClick,
                onNavigateToSearch = onNavigateToSearch,
            )
        }
    }
}
