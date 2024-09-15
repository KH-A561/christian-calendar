package ru.akhilko.christian_calendar.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarViewMonth
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CalendarApp() {

}

@Composable
fun MainScreen() {
    Scaffold(bottomBar = {
        NavigationBar {
            icons.forEach { item ->
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { ... },
                    icon = { ... })
            }
        }
    }) {

    }
}

data class BarItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Month",
            icon = Icons.Outlined.CalendarViewMonth,
            route = "home"
        ),
        BarItem(
            title = "Contacts",
            icon = Icons.Filled.Face,
            route = "contacts"
        ),
        BarItem(
            title = "About",
            icon = Icons.Filled.Info,
            route = "about"
        )
    )
}