package ru.akhilko.day.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.akhilko.day.DayRoute

const val DAY_ID_SAVED_STATE_KEY = "dayId"
const val DAY_GRAPH_ROUTE = "day_graph"
const val DAY_ROUTE_BASE = "day_route"
const val DAY_ROUTE = "$DAY_ROUTE_BASE/{$DAY_ID_SAVED_STATE_KEY}"

fun NavController.navigateToDay(dayId: String, navOptions: NavOptions? = null) {
    navigate("$DAY_ROUTE_BASE/$dayId", navOptions)
}

fun NavGraphBuilder.dayScreen(
    onBack: () -> Unit,
    onNavigateToDay: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    navigation(
        route = DAY_GRAPH_ROUTE,
        startDestination = DAY_ROUTE,
    ) {
        composable(
            route = DAY_ROUTE,
            arguments = listOf(
                navArgument(DAY_ID_SAVED_STATE_KEY) { type = NavType.StringType }
            )
        ) {
            DayRoute(
                onBack = onBack,
                onNavigateToDay = onNavigateToDay,
            )
        }
    }
}
