package ru.akhilko.day.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val DAY_ROUTE = "day_route"

fun NavController.navigateToDay(navOptions: NavOptions) = navigate(DAY_ROUTE, navOptions)

fun NavGraphBuilder.dayScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = DAY_ROUTE) {
//        MonthRoute(onTopicClick, onShowSnackbar)
    }
}