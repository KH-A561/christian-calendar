package ru.akhilko.christian_calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.month.MonthRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплэш-скрин. Он автоматически скроется,
        // как только первый кадр Compose будет отрисован.
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            // Подключаем вашу тему
            CalendarTheme {
                // Отображаем главный экран (NavHost или прямо ваш MonthRoute)
                MonthRoute(onDayClick = { /* TODO: handle day click */ })
            }
        }
    }
}
