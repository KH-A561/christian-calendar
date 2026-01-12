package ru.akhilko.christian_calendar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons as MaterialIcons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.akhilko.christian_calendar.navigation.ChristianCalendarNavHost
import ru.akhilko.christian_calendar.navigation.TopLevelDestination
import ru.akhilko.core.designsystem.component.CalendarBackground
import ru.akhilko.core.designsystem.component.CalendarGradientBackground
import ru.akhilko.core.designsystem.component.CalendarNavigationSuiteScaffold
import ru.akhilko.core.designsystem.component.CalendarTopAppBar
import ru.akhilko.core.designsystem.icon.Icons
import ru.akhilko.core.designsystem.theme.GradientColors
import ru.akhilko.core.designsystem.theme.LocalGradientColors
import ru.akhilko.feature.settings.R as settingsR
import ru.akhilko.month.MonthViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ChristianCalendarApp(
    appState: ChristianCalendarAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val monthViewModel: MonthViewModel = hiltViewModel()
    CalendarBackground(modifier = modifier) {
        CalendarGradientBackground(
            gradientColors = if (appState.currentTopLevelDestination == TopLevelDestination.MONTH) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {

            ChristianCalendarAppInternal(
                appState = appState,
                windowAdaptiveInfo = windowAdaptiveInfo,
                monthViewModel = monthViewModel
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
)
internal fun ChristianCalendarAppInternal(
    appState: ChristianCalendarAppState,
    monthViewModel: MonthViewModel,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val currentDestination = appState.currentDestination
    val snackbarHostState = remember { SnackbarHostState() }

    CalendarNavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected = currentDestination
                    .isTopLevelDestinationInHierarchy(destination)
                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelDestination(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.iconTextId)) },
                    modifier =
                    Modifier
                        .testTag("CalendarNavItem")
                        .then(Modifier),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                // Show the top app bar on top level destinations.
                val destination = appState.currentTopLevelDestination
                val shouldShowTopAppBar = destination != null
                if (destination != null) {
                    CalendarTopAppBar(
                        // Убираем текст "Month", если выбран календарь
                        titleRes = if (destination == TopLevelDestination.MONTH) 0 else destination.titleTextId,
                        navigationIcon = Icons.Search,
                        navigationIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_top_app_bar_navigation_icon_description,
                        ),
                        // Кнопка "Сегодня" в действиях
                        actionIcon = MaterialIcons.Default.Today,
                        actionIconContentDescription = "К сегодня",
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        onActionClick = { 
                            if (destination == TopLevelDestination.MONTH) {
                                monthViewModel.onTodayClick()
                            }
                        },
                        onNavigationClick = { appState.navigateToSearch() },
                    )
                }

                Box(
                    modifier = Modifier.consumeWindowInsets(
                        if (shouldShowTopAppBar) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),
                ) {
                    ChristianCalendarNavHost(
                        appState = appState,
                        onShowSnackbar = { message, action ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = Short,
                            ) == ActionPerformed
                        },
                        monthViewModel = monthViewModel
                    )
                }
            }
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
