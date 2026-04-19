package ru.akhilko.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

enum class JulianStyle { Inline, Large }

@Composable
fun JulianDateLabel(
    julianYear: Int,
    julianMonth: Int,
    julianDay: Int,
    modifier: Modifier = Modifier,
    style: JulianStyle = JulianStyle.Inline,
) {
    val monthGenitive = remember(julianMonth) { monthGenitiveRu(julianMonth) }
    val text = "ст. ст. $julianDay $monthGenitive"
    Text(
        text = text,
        style = when (style) {
            JulianStyle.Inline -> MaterialTheme.typography.labelSmall
            JulianStyle.Large -> MaterialTheme.typography.bodyMedium
        },
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

private fun monthGenitiveRu(month: Int): String = when (month) {
    1 -> "января"
    2 -> "февраля"
    3 -> "марта"
    4 -> "апреля"
    5 -> "мая"
    6 -> "июня"
    7 -> "июля"
    8 -> "августа"
    9 -> "сентября"
    10 -> "октября"
    11 -> "ноября"
    12 -> "декабря"
    else -> ""
}
