package ru.akhilko.month.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.akhilko.month.MonthRoute

const val CENTERED_MONTH = "centeredMonth"
const val CENTERED_YEAR = "centeredYear"
const val DAY_SELECTED = "daySelected"
const val MONTH_ROUTE = "month_route"

fun NavController.navigateToMonth(navOptions: NavOptions) = navigate(MONTH_ROUTE, navOptions)

fun NavGraphBuilder.monthScreen() {
    composable(route = MONTH_ROUTE) {
        MonthRoute()
    }
}