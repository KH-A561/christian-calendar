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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплэш-скрин. Он автоматически скроется,
        // как только первый кадр Compose будет отрисован.
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val appState = ChristianCalendarAppState(
                navController = rememberNavController()
            )

            // Подключаем вашу тему
            CalendarTheme {
                // Отображаем главный компонент приложения
                ChristianCalendarApp(
                    appState = appState,
                    windowAdaptiveInfo = currentWindowAdaptiveInfo()
                )
            }
        }
    }
}
