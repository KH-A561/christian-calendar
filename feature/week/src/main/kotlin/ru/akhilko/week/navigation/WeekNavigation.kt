package ru.akhilko.week.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.akhilko.week.WeekRoute
import ru.akhilko.core.ui.state.SelectedDayHolder

const val WEEK_ROUTE = "week_route"

fun NavController.navigateToWeek(navOptions: NavOptions) = navigate(WEEK_ROUTE, navOptions)

fun NavGraphBuilder.weekScreen(
    onDayClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    selectedDayHolder: SelectedDayHolder,
) {
    composable(route = WEEK_ROUTE) {
        WeekRoute(
            onDayClick = onDayClick,
            onShowSnackbar = onShowSnackbar,
            selectedDayHolder = selectedDayHolder
        )
    }
}