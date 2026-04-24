package ru.akhilko.day.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.theme.ColorEasterSurface
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorGreatFeast
import ru.akhilko.core.designsystem.theme.ColorRemembrance
import ru.akhilko.core.designsystem.theme.ColorTwelveFeast
import ru.akhilko.core.designsystem.component.DayTypeBadgeRow
import ru.akhilko.core.designsystem.component.JulianDateLabel
import ru.akhilko.core.designsystem.component.JulianStyle
import ru.akhilko.core.ui.format.monthGenitiveRu
import ru.akhilko.core.ui.mapper.CalendarDayPresentation

@Composable
fun DayHero(
    presentation: CalendarDayPresentation,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val heroBg = when {
        presentation.badges.contains(BadgeKind.EASTER) -> ColorEasterSurface
        presentation.badges.contains(BadgeKind.TWELVE) -> ColorTwelveFeast.copy(alpha = 0.10f)
        presentation.badges.contains(BadgeKind.GREAT) -> ColorGreatFeast.copy(alpha = 0.10f)
        presentation.badges.contains(BadgeKind.REMEMBRANCE) -> ColorRemembrance.copy(alpha = 0.08f)
        presentation.badges.contains(BadgeKind.FAST) -> ColorFast.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            // «Бумажный» белый hero-блок, возвышающийся над кремовым фоном страницы,
            // с тонкой тёплой рамкой из outlineVariant (PaperBorder).
            .background(heroBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape,
            )
            .padding(PaddingValues(horizontal = 20.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Строка 1: [большое число] [месяц год]  ...................  [Пн]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = presentation.gregorianDayNum.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.padding(horizontal = 6.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "${monthGenitiveRu(presentation.gregorianMonth)} ${presentation.gregorianYear}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                JulianDateLabel(
                    julianYear = presentation.julianYear,
                    julianMonth = presentation.julianMonth,
                    julianDay = presentation.julianDay,
                    style = JulianStyle.Inline,
                )
            }
            Text(
                text = presentation.weekdayShortRu.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 18.dp),
            )
        }

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

        // weekText показываем только если он отличается от title —
        // иначе получается визуальное дублирование.
        val weekText = presentation.weekText
        if (!weekText.isNullOrBlank() && weekText != presentation.title) {
            Text(
                text = weekText,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
