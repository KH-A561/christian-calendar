package ru.akhilko.christian_calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.akhilko.christian_calendar.ui.ChristianCalendarApp
import ru.akhilko.christian_calendar.ui.ChristianCalendarAppState
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.state.SelectedDayHolder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var selectedDayHolder: SelectedDayHolder

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val appState = ChristianCalendarAppState(
                navController = rememberNavController(),
                selectedDayHolder = selectedDayHolder,
            )

            CalendarTheme {
                ChristianCalendarApp(
                    appState = appState,
                    windowAdaptiveInfo = currentWindowAdaptiveInfo()
                )
            }
        }
    }
}
