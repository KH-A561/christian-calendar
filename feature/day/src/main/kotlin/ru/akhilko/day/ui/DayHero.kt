package ru.akhilko.day.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.DayTypeBadgeRow
import ru.akhilko.core.designsystem.component.JulianDateLabel
import ru.akhilko.core.designsystem.component.JulianStyle
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun DayHero(
    presentation: CalendarDayPresentation,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(PaddingValues(horizontal = 20.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = presentation.gregorianDayNum.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = presentation.monthYearRu,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        JulianDateLabel(
            julianYear = presentation.julianYear,
            julianMonth = presentation.julianMonth,
            julianDay = presentation.julianDay,
            style = JulianStyle.Large,
        )
        Text(
            text = presentation.weekdayRu,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (presentation.badges.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            DayTypeBadgeRow(presentation.badges)
        }
        if (presentation.title.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = presentation.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        presentation.weekText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
