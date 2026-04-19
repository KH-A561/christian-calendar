package ru.akhilko.day.ui.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.akhilko.core.ui.format.monthGenitiveRu
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun GeneralTabContent(
    presentation: CalendarDayPresentation,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoRow(
                label = "Дата",
                value = "${presentation.gregorianDayNum} ${monthGenitiveRu(presentation.gregorianMonth)} ${presentation.gregorianYear}",
            )
            InfoRow(
                label = "По старому стилю",
                value = "${presentation.julianDay} ${monthGenitiveRu(presentation.julianMonth)} ${presentation.julianYear}",
            )
            InfoRow(label = "День недели", value = presentation.weekdayRu)
            presentation.weekText?.let {
                InfoRow(label = "Литургический период", value = it)
            }
            if (presentation.title.isNotBlank()) {
                InfoRow(label = "Название", value = presentation.title)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
