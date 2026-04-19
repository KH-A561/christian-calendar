package ru.akhilko.day.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class DayTab(val label: String) {
    GENERAL("Общее"),
    FAST("Пост"),
    READINGS("Чтения"),
    SAINTS("Святые"),
}

@Composable
fun DayTabs(
    selected: DayTab,
    onSelect: (DayTab) -> Unit,
) {
    TabRow(selectedTabIndex = selected.ordinal) {
        DayTab.entries.forEach { tab ->
            Tab(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                text = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
    }
}
