package ru.akhilko.week.ui.search

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind

private data class FilterItem(
    val title: String,
    val badge: BadgeKind?,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchModeBar(
    query: String,
    selectedFilter: BadgeKind?,
    onQueryChanged: (String) -> Unit,
    onFilterSelected: (BadgeKind?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = listOf(
        FilterItem("Все", null),
        FilterItem("Великий", BadgeKind.GREAT),
        FilterItem("Праздник", BadgeKind.FEAST),
        FilterItem("Пост", BadgeKind.FAST),
        FilterItem("Память", BadgeKind.REMEMBRANCE),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск по дням") },
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { item ->
                FilterChip(
                    selected = selectedFilter == item.badge,
                    onClick = { onFilterSelected(item.badge) },
                    label = { Text(item.title) },
                )
            }
        }
    }
}
