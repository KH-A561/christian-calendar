package ru.akhilko.christian_calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.akhilko.christian_calendar.ui.ChristianCalendarApp
import ru.akhilko.christian_calendar.ui.ChristianCalendarAppState
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.state.SelectedDayHolder
import ru.akhilko.settings.SettingsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var selectedDayHolder: SelectedDayHolder

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDark by settingsViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val appState = ChristianCalendarAppState(
                navController = rememberNavController(),
                selectedDayHolder = selectedDayHolder,
            )

            CalendarTheme(darkTheme = isDark) {
                ChristianCalendarApp(
                    appState = appState,
                    windowAdaptiveInfo = currentWindowAdaptiveInfo()
                )
            }
        }
    }
}
