package ru.akhilko.month.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MONTH_ROUTE = "month_route"

fun NavController.navigateToMonth(navOptions: NavOptions) = navigate(MONTH_ROUTE, navOptions)

fun NavGraphBuilder.monthScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = MONTH_ROUTE) {
//        MonthRoute(onTopicClick, onShowSnackbar)
    }
}