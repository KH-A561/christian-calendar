package ru.akhilko.month.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarMonth
import ru.akhilko.christian_calendar.core.data.model.CalendarDayResource
import ru.akhilko.christian_calendar.core.domain.model.MonthSummary
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.core.designsystem.theme.ColorEaster
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorFeast
import ru.akhilko.core.designsystem.theme.ColorGreat
import ru.akhilko.core.designsystem.theme.ColorGreatFeast
import ru.akhilko.core.designsystem.theme.ColorRemembrance
import ru.akhilko.core.designsystem.theme.ColorTwelveFeast
import java.time.LocalDate
import java.time.YearMonth

/**
 * Свёрнутая карточка-легенда: только список цветовых меток + кнопка
 * «События месяца», которая открывает [MonthEventsSheet] (модальный bottom sheet).
 *
 * Развёрнутый список событий вынесен в отдельный sheet, чтобы внутренний скролл
 * не конфликтовал с paged-flingом VerticalCalendar.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthEventSummary(
    summaries: Map<Int, List<MonthSummary>>,
    month: CalendarMonth,
    dayResources: List<CalendarDayResource>,
    onShowEvents: (CalendarMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary =
        summaries[month.yearMonth.year]?.find { it.month == month.yearMonth.monthValue } ?: return
    if (summary.highlightedDays.isEmpty() && summary.highlightedPeriods.isEmpty()) return

    val monthDays = dayResources
        .asSequence()
        .filter {
            it.day.gregorianYear == month.yearMonth.year &&
                    it.day.gregorianMonth == month.yearMonth.monthValue
        }
    val monthDayTypes = monthDays.flatMap { it.day.dayTypes }.filter { it != DayType.UNKNOWN }
        .toSet()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Легенда",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    monthDayTypes
                        .sortedBy { it.ordinal }
                        .forEach {
                            LegendItem(it.getColor(), it.getLabel())
                        }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowEvents(month) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "События месяца",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Открыть события месяца",
                )
            }
        }
    }
}

internal fun monthEventRows(
    summary: MonthSummary,
    dayResources: List<CalendarDayResource>,
    yearMonth: YearMonth,
): List<MonthEventRowData> {
    val gregorianDayToMonthDays = dayResources
        .filter {
            it.day.gregorianYear == yearMonth.year &&
                    it.day.gregorianMonth == yearMonth.monthValue
        }
        .associateBy { it.day.gregorianDay }
    val rows = mutableListOf<MonthEventRowData>()
    summary.highlightedPeriods.forEach { period ->
        val resource = gregorianDayToMonthDays[period.startDay]
        rows += MonthEventRowData(
            dateLabel = "${period.startDay}-${period.endDay}",
            title = period.name,
            color = eventColorFor(resource, isPeriod = true),
            navigateId = resource?.id ?: dayIdFrom(yearMonth, period.startDay),
        )
    }
    summary.highlightedDays.forEach { day ->
        val resource = gregorianDayToMonthDays[day.dayOfMonth]
        rows += MonthEventRowData(
            dateLabel = day.dayOfMonth.toString(),
            title = day.name,
            color = eventColorFor(resource, isPeriod = false),
            navigateId = resource?.id ?: dayIdFrom(yearMonth, day.dayOfMonth),
        )
    }
    return rows
}

internal data class MonthEventRowData(
    val dateLabel: String,
    val title: String,
    val color: Color,
    val navigateId: String,
)

@Composable
internal fun EventRow(
    dateLabel: String,
    title: String,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, CircleShape)
                .clip(CircleShape),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun eventColorFor(
    dayResource: CalendarDayResource?,
    isPeriod: Boolean,
): Color {
    if (dayResource == null) {
        return if (isPeriod) ColorFast else ColorGreat
    }
    val dayTypes = dayResource.day.dayTypes
    return when {

        dayTypes.contains(DayType.EASTER) -> ColorEaster
        dayTypes.contains(DayType.TWELVE_GREAT_FEASTS) -> ColorTwelveFeast.copy(alpha = 0.55f)
        dayTypes.contains(DayType.GREAT_FEAST) -> ColorGreatFeast
        dayTypes.contains(DayType.COMMEMORATION) -> ColorRemembrance
        dayTypes.contains(DayType.FAST) || dayTypes.contains(DayType.LONG_FAST) -> ColorFast
        isPeriod -> ColorFast
        else -> ColorGreat
    }
}

private fun dayIdFrom(yearMonth: YearMonth, day: Int): String =
    LocalDate.of(yearMonth.year, yearMonth.monthValue, day).toString()

fun DayType.getColor(): Color = when (this) {
    DayType.EASTER -> ColorEaster
    DayType.TWELVE_GREAT_FEASTS -> ColorTwelveFeast.copy(alpha = 0.55f)
    DayType.GREAT_FEAST -> ColorGreatFeast
    DayType.FEAST -> ColorFeast
    DayType.FAST -> ColorFast
    DayType.LONG_FAST -> ColorFast
    DayType.COMMEMORATION -> ColorRemembrance
    DayType.UNKNOWN -> Color.Gray
}

fun DayType.getLabel(): String = when (this) {
    DayType.EASTER -> "Пасха!"
    DayType.TWELVE_GREAT_FEASTS -> "Двунадесятый праздник"
    DayType.GREAT_FEAST -> "Великий праздник"
    DayType.FEAST -> "Праздник"
    DayType.FAST -> "Пост"
    DayType.LONG_FAST -> "Многодневный пост"
    DayType.COMMEMORATION -> "Поминовение"
    DayType.UNKNOWN -> "Неизвестно"
}